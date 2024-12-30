package com.shopme.admin;

import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import org.apache.catalina.authenticator.SpnegoAuthenticator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private UserService service;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping()
    public String viewHomePage(HttpSession session, Model model) throws UserNotFoundException {
//        String userId = session.getAttribute("userId").toString();
//        if (userId != null) {
//            User user = service.get(Integer.parseInt(userId));
//            model.addAttribute("user", user);
//        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ShopmeUserDetails userDetails = (ShopmeUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        System.out.println("User: " + user);
        model.addAttribute("user", user);

        return "index.html";
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        return "login.html";
    }

    // Phương thức này không có tác dụng trong spring security bởi vì spring security sẽ xử lý việc login sau khi submit form login thì spring security thực hiện xác thực thông tin
    @PostMapping("/login")
    public String processLogin(@PathParam("email") String email, @PathParam("password") String password, RedirectAttributes redirectAttributes, HttpSession session, Model model) {
        System.out.println("Login form is submitted");

        List<User> listUsers = service.listAll();
        for (User user : listUsers) {
            if (user.getEmail().equals(email) && passwordEncoder.matches(password, user.getPassword())) {
                if (user.isEnabled()) {
                    System.out.println("Login success");
                    session.setAttribute("userId", user.getId());
                    model.addAttribute("user", user);
                    return "redirect:/";
                } else {
                    redirectAttributes.addFlashAttribute("error", "Your account is not activated yet");
                    return "redirect:/login";
                }
            }
        }

        redirectAttributes.addFlashAttribute("error", "Invalid email or password");
        return "redirect:/login";
    }

}