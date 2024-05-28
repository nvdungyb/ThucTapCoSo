package com.shopme;

import com.shopme.category.CategoryRepository;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Product;
import com.shopme.product.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MainController {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductService productService;

    @GetMapping("")
    public String viewHomePage(Model model) {
        List<Product> listProducts = productService.findAll();

        model.addAttribute("listProducts", listProducts);

        return "index";
    }

    @GetMapping("/categories")
    public String viewCategoriesPage(Model model) {
        List<Category> listCategories = categoryRepository.findAllEnabled();

        model.addAttribute("listCategories", listCategories);
        return "categries";
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
