package com.shopme.admin.user;

import com.shopme.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService service;

    @GetMapping("/users")   // Khi người dùng truy cập vào đường dẫn /users
    public String listAll(Model model) {
        List<User> listUsers = service.listAll();
        model.addAttribute("listUsers", listUsers);

        return "users";         // Trả dữ liệu về view users.html
    }
}

/* Modle là một interface, cung cấp các phương thức để them dữ liệu vào model và sau đó
truy cập dữ liệu này từ view để hiện thị trên giao diện người dùng.
_ Các phương thức cơ bản trong Model: addAttribute(String attr, Object), getAttribute().
 */