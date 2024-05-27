package com.shopme.product;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;

    @GetMapping("/c/{category_alias}")
    public String viewCategoryFirstPage(@PathVariable("category_alias") String alias, Model model) {
        return viewCategoryByPage(alias, 1, model);
    }

    @GetMapping("/c/{category_alias}/page/{pageNum}")
    public String viewCategoryByPage(@PathVariable("category_alias") String alias, @PathVariable("pageNum") int pageNum, Model model) {
        Category category = categoryService.getCategory(alias);

        if (category == null) {
            return "index";
        }

        List<Category> listCategoryParents = categoryService.listCategoryParents(category);
        Page<Product> pageProducts = productService.listByCategory(pageNum, category.getId());
        List<Product> listProducts = pageProducts.getContent();

        long startCount = (pageNum - 1) * productService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + productService.PRODUCTS_PER_PAGE - 1;

        if (endCount > pageProducts.getTotalElements()) {
            endCount = pageProducts.getTotalElements();
        }

        model.addAttribute("totalPages", pageProducts.getTotalPages());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalItems", pageProducts.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("listProducts", listProducts);
        model.addAttribute("listCategoryParents", listCategoryParents);
        model.addAttribute("pageTitle", category.getName());
        return "products_by_category";
    }

    @GetMapping("/p/{product_alias}")
    public String viewProductDetail(@PathVariable("product_alias") String alias, Model model) {
        try {
            Product product = productService.getProduct(alias);
            List<Category> listCategoryParents = categoryService.listCategoryParents(product.getCategory());

            model.addAttribute("product", product);
            model.addAttribute("listCategoryParents", listCategoryParents);
            model.addAttribute("pageTitle", product.getName());
            return "product/product_detail";
        } catch (Exception e) {
        }
        return "index";
    }
}
