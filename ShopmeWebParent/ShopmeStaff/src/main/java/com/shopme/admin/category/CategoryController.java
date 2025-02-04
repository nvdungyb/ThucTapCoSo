package com.shopme.admin.category;

import com.shopme.admin.FileUploadUtil;
import com.shopme.common.shop.Category;
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
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/categories")
    public String firstPage(Model model) {
        return listByPage(1, null, model);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum, @RequestParam(name = "keyword", required = false) String keyword, Model model) {
        Page<Category> listCategoriesPage = categoryService.listByPage(pageNum, keyword);
        List<Category> listCategories = listCategoriesPage.getContent();

        long startCount = (pageNum - 1) * CategoryService.CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.CATEGORIES_PER_PAGE - 1;

        if (endCount > listCategoriesPage.getTotalElements()) {
            endCount = listCategoriesPage.getTotalElements();
        }

        model.addAttribute("totalPages", listCategoriesPage.getTotalPages());
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("totalItems", listCategoriesPage.getTotalElements());
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("listCategories", listCategories);

        return "categories/category";
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        categoryService.updateCategoryEnabledStatus(id, enabled);
        if (enabled)
            redirectAttributes.addFlashAttribute("message", "The category ID " + id + " has been enabled");
        else
            redirectAttributes.addFlashAttribute("message", "The category ID " + id + " has been disabled");

        return "redirect:/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        String pageTitle = "Create New Category";
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("listCategories", listCategories);

        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("fileImage") MultipartFile fileImage, RedirectAttributes redirectAttributes) throws IOException {
        if (!fileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(fileImage.getOriginalFilename());
            category.setImage(fileName);

            Category savedCategory = categoryService.saveCategory(category);
            String uploadDir = "uploads/categories-images/" + savedCategory.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, fileImage);
        } else {
            categoryService.saveCategory(category);
        }

        redirectAttributes.addFlashAttribute("message", "The category has been saved successfully.");
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategoriy(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        Category category = categoryService.get(id);
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        model.addAttribute("category", category);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Edit Category (ID: " + id + ")");

        return "categories/category_form";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.delete(id);
            String categoryDir = "uploads-categories/" + id;
            FileUploadUtil.removeDir(categoryDir);
            redirectAttributes.addFlashAttribute("message", "The category ID " + id + " has been deleted successfully");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/categories";
    }

}
