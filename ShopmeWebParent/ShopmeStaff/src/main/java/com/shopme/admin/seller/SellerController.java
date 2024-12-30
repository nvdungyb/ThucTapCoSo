package com.shopme.admin.seller;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.Seller;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class SellerController {
    @Autowired
    private SellerService service;

    // Khi người dùng truy cập vào đường dẫn /users
    @GetMapping("/users")
    public String listFirstPage(Model model) {
        return listByPage(1, null, model);
    }

    @GetMapping("/users/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, @RequestParam(name = "keyword", required = false) String keyword, Model model) {
        Page<Seller> page = service.listByPage(pageNum, keyword);
        List<Seller> listUsers = page.getContent();

        long startCount = (pageNum - 1) * SellerService.USERS_PER_PAGE + 1;
        long endCount = startCount + SellerService.USERS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }

        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("totalItems", page.getTotalElements());
        model.addAttribute("listUsers", listUsers);
        return "users/user";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> listRoles = service.listRoles();

        Seller seller = new Seller();
        seller.setEnabled(true);
        model.addAttribute("seller", seller);
        model.addAttribute("listRoles", listRoles);
        model.addAttribute("pageTitle", "Create New User");

        return "users/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(Seller seller, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            seller.setProfilePicture(fileName);
            User savedUser = service.save(seller);
            String uploadDir = "uploads/seller-photos/" + savedUser.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (seller.getProfilePicture() == null) seller.setProfilePicture(null);
            service.save(seller);
        }

        redirectAttributes.addFlashAttribute("message", "The seller has been saved successfully");
        return "redirect:/users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        try {
            User user = service.get(id);
            List<Role> listRoles = service.listRoles();

            model.addAttribute("user", user);
            model.addAttribute("pageTitle", "Edit User (ID: " + id + ")");
            model.addAttribute("listRoles", listRoles);

            return "users/user_form";
        } catch (SellerNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", "Could not find any user with ID " + id);
            return "redirect:/users";
        }
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes, Model model) {
        try {
            service.delete(id);
            redirectAttributes.addFlashAttribute("message", "The user ID " + id + " has been deleted successfully");
        } catch (SellerNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", "Could not find any user with id " + id);
        }

        return "redirect:/users";
    }

    @GetMapping("/users/{id}/enabled/{status}")
    public String updateUserEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        service.updateUserEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The user ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/users";
    }
}

/* Modle là một interface, cung cấp các phương thức để them dữ liệu vào model và sau đó
truy cập dữ liệu này từ view để hiện thị trên giao diện người dùng.
_ Các phương thức cơ bản trong Model: addAttribute(String attr, Object), getAttribute().
 */