package com.shopme.admin;

import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.common.entity.User;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping()
    public String viewHomePage(HttpSession session, Model model) throws UserNotFoundException {
        String userId = session.getAttribute("userId").toString();
        if (userId != null) {
            User user = service.get(Integer.parseInt(userId));
            model.addAttribute("user", user);
        }
        return "index.html";
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@PathParam("email") String email, @PathParam("password") String password, RedirectAttributes redirectAttributes, HttpSession session, Model model) {
        System.out.println("Login form is submitted");

        List<User> listUsers = service.listAll();
        for (User user : listUsers) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
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

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("userId");
        return "redirect:/login";
    }


}