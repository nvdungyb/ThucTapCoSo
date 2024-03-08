package com.shopme.admin.user;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService service;

    // Khi người dùng truy cập vào đường dẫn /users
    @GetMapping("/users")
    public String listAll(Model model) {
        List<User> listUsers = service.listAll();
        model.addAttribute("listUsers", listUsers);

        // Trả dữ liệu về view users.html
        return "users";
    }

    @GetMapping("/users/new")
    public String newUser(Model model) {
        List<Role> listRoles = service.listRoles();

        User user = new User();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("listRoles", listRoles);

        return "user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(User user, RedirectAttributes redirectAttributes) {
        System.out.println(user);
        service.save(user);

        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
        return "redirect:/users";
    }
}

/* Modle là một interface, cung cấp các phương thức để them dữ liệu vào model và sau đó
truy cập dữ liệu này từ view để hiện thị trên giao diện người dùng.
_ Các phương thức cơ bản trong Model: addAttribute(String attr, Object), getAttribute().
 */