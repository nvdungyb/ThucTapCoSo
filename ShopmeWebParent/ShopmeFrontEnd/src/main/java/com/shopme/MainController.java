package com.shopme;

import com.shopme.category.CategoryRepository;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {
    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("")
    public String viewHomePage(Model model) {
        List<Category> listCategories = categoryRepository.findAllEnabled();

        model.addAttribute("listCategories", listCategories);
        return "index";
    }

    @GetMapping("/login")
    public String viewLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        Customer customer = new Customer();
        model.addAttribute("customer", customer);
        return "register";
    }

}
