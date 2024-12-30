package com.shopme.admin.brand;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryService;
import com.shopme.common.shop.Brand;
import com.shopme.common.shop.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class BrandController {
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/brands")
    public String listFirstPage(Model model) {
        return listByPage(1, null, model);
    }

    @GetMapping("/brands/page/{pageNum}")
    public String listByPage(@PathVariable("pageNum") Integer pageNum, @RequestParam(name = "keyword", required = false) String keyword, Model model) {
        Page<Brand> brandPages = brandService.listByPage(pageNum, keyword);
        List<Brand> listBrands = brandPages.getContent();

        int totalItems = (int) brandPages.getTotalElements();
        int startCount = (pageNum - 1) * BrandService.BRANDS_PER_PAGE + 1;
        int endCount = startCount + BrandService.BRANDS_PER_PAGE - 1;
        if (endCount > totalItems)
            endCount = totalItems;

        model.addAttribute("listBrands", listBrands);
        model.addAttribute("totalPages", brandPages.getTotalPages());
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("currentPage", pageNum);

        return "brands/brand";
    }

    @GetMapping("/brands/new")
    public String newBrand(Model model) {
        List<Category> listCategories = categoryService.listCategoriesUsedInForm();

        Brand brand = new Brand();
        model.addAttribute("brand", brand);
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Brand");

        return "brands/brand_form";
    }

    @PostMapping("/brands/save")
    public String saveBrand(Brand brand, @RequestParam("fileImage") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        Brand savedBrand = null;
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            brand.setLogo(fileName);

            savedBrand = brandService.save(brand);

            String uploadDir = "uploads/brandLogo/" + savedBrand.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        } else {
            if (brand.getLogo().isEmpty()) brand.setLogo(null);
            savedBrand = brandService.save(brand);
        }

        redirectAttributes.addFlashAttribute("message", "The brand ID " + savedBrand.getId() + " has been saved successfully.");

        return "redirect:/brands";
    }

    @GetMapping("/brands/edit/{id}")
    public String editBrand(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirecAttributes) {
        try {
            Brand editBrand = brandService.get(id);
            List<Category> listCategories = categoryService.listCategoriesUsedInForm();
            String pageTitle = "Edit Brand (ID: " + id + ")";

            model.addAttribute("brand", editBrand);
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("pageTitle", pageTitle);

            return "brands/brand_form";
        } catch (Exception e) {
            redirecAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/brands";
        }
    }

    @GetMapping("/brands/delete/{id}")
    public String deleteBrand(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            brandService.delete(id);
            String brandDir = "uploads/brandLogo/" + id;
            FileUploadUtil.removeDir(brandDir);
            redirectAttributes.addFlashAttribute("message", "The brand ID " + id + " has been deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }

        return "redirect:/brands";
    }
}
