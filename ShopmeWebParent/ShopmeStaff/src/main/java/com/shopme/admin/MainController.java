package com.shopme.admin;

import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.seller.SellerNotFoundException;
import com.shopme.admin.seller.SellerService;
import com.shopme.common.entity.Seller;
import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class MainController {
    @Autowired
    private SellerService sellerService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping()
    public String viewHomePage(HttpSession session, Model model) throws SellerNotFoundException {
        // todo: need to change that
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ShopmeUserDetails userDetails = (ShopmeUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        System.out.println("User: " + user);
        model.addAttribute("user", user);

        return "index.html";
    }

    @GetMapping("/register")
    public String viewRegisterPage(Model model) {
        Seller user = new Seller();
        user.setEnabled(true);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Create New Account");
        return "register.html";
    }

    @PostMapping("/register")
    public String processRegister(Seller seller, RedirectAttributes redirectAttributes) {
        sellerService.registerSeller(seller);
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        return "login.html";
    }

    // Phương thức này không có tác dụng trong spring security bởi vì spring security sẽ xử lý việc login sau khi submit form login thì spring security thực hiện xác thực thông tin
//    @PostMapping("/login")
//    public String processLogin(@PathParam("email") String email, @PathParam("password") String password, RedirectAttributes redirectAttributes, HttpSession session, Model model) {
//        System.out.println("Login form is submitted");
//
//        List<Seller> listUsers = sellerService.listAll();
//        for (Seller user : listUsers) {
//            if (user.getEmail().equals(email) && passwordEncoder.matches(password, user.getPassword())) {
//                if (user.isEnabled()) {
//                    System.out.println("Login success");
//                    session.setAttribute("userId", user.getId());
//                    model.addAttribute("user", user);
//                    return "redirect:/";
//                } else {
//                    redirectAttributes.addFlashAttribute("error", "Your account is not activated yet");
//                    return "redirect:/login";
//                }
//            }
//        }
//
//        redirectAttributes.addFlashAttribute("error", "Invalid email or password");
//        return "redirect:/login";
//    }

}