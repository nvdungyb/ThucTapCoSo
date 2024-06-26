package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.common.entity.Brand;
import com.shopme.common.entity.Product;
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
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;

    @GetMapping("/products")
    public String listFirstPage(Model model) {
        return listByPage(1, null, model);
    }

    @GetMapping("/products/page/{pagenum}")
    public String listByPage(@PathVariable(name = "pagenum") Integer pagenum, @RequestParam(name = "keyword", required = false) String keyword, Model model) {
        Page<Product> page = productService.listByPage(pagenum, keyword);

        List<Product> listProducts = page.getContent();
        long totalItems = page.getTotalElements();
        long startCount = (pagenum - 1) * ProductService.PRODUCTS_PER_PAGE + 1;
        long endCount = startCount + ProductService.PRODUCTS_PER_PAGE - 1;
        if (endCount > totalItems)
            endCount = totalItems;
        long totalPages = page.getTotalPages();

        model.addAttribute("listProducts", listProducts);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("startCount", startCount);
        model.addAttribute("endCount", endCount);
        model.addAttribute("currentPage", pagenum);
        model.addAttribute("totalPages", totalPages);

        return "products/products";
    }

    @GetMapping("/products/new")
    public String newProduct(Model model) {
        List<Brand> listBrand = brandService.findAll();

        Product product = new Product();
        product.setEnabled(true);
        product.setInStock(true);
        model.addAttribute("product", product);
        model.addAttribute("listBrand", listBrand);
        model.addAttribute("pageTitle", "Create New Product");

        return "products/product_form";
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, @RequestParam(name = "fileImage", required = false) MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {

        if(!file.isEmpty()){
            String fileName = file.getOriginalFilename();
            product.setMainImage(fileName);

            Product savedProduct = productService.save(product);

            String uploadDir = "uploads/product-images/" + savedProduct.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        }else{
            productService.save(product);
        }

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return "redirect:/products";
    }

    @GetMapping("/products/{id}/enabled/{status}")
    public String updateProductEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled, RedirectAttributes redirectAttributes) {
        productService.updateProductEnabledStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        String message = "The product ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);
        return "redirect:/products";
    }

    @GetMapping("/products/delete/{id}")
    public String deleteProduct(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        productService.delete(id);
        redirectAttributes.addFlashAttribute("message", "The product ID " + id + " has been deleted successfully");
        return "redirect:/products";
    }

    @GetMapping("/products/edit/{id}")
    public String editProductById(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.findById(id);
            List<Brand> listBrand = brandService.findAll();
            model.addAttribute("product", product);
            model.addAttribute("listBrand", listBrand);
            model.addAttribute("pageTitle", "Edit Product (ID: " + id + ")");
            return "products/product_form";
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/products";
        }
    }
}
