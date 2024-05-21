package com.shopme.admin.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CategoryRestController {
    @Autowired
    CategoryService categoryService;

    @PostMapping("/categories/check_unique")
    public String checkIsUnique(Integer id, String name, String alias) {
        if(id == null || id == 0) {
            return categoryService.isUnique(name, alias) ? "OK" : "Duplicated";
        }
        return "OK"; // Nếu id != null và id != 0 thì không cần kiểm tra unique. vì đó là trường hợp update.
    }
}
