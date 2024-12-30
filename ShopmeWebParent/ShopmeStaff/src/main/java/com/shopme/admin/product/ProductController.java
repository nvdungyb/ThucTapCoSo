package com.shopme.admin.product;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.brand.BrandService;
import com.shopme.admin.security.ShopmeUserDetails;
import com.shopme.common.entity.User;
import com.shopme.common.shop.*;
import com.shopme.common.utils.Currency;
import com.shopme.common.utils.Gender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private BrandService brandService;

    Logger logger = LoggerFactory.getLogger(ProductController.class);

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

    @GetMapping("/products/new/{productType}")
    public String newProduct(Model model, @PathVariable("productType") String productType) {
        List<Brand> listBrand = brandService.findAll();
        model.addAttribute("listBrand", listBrand);
        model.addAttribute("listCurrency", Arrays.stream(Currency.values()).collect(Collectors.toList()));
        model.addAttribute("pageTitle", "Create New Product");

        logger.info("Product Type: " + productType);

        switch (productType.toLowerCase()) {
            case "laptop":
                Laptop laptop = new Laptop();
                model.addAttribute("product", laptop);
                return "products/product_form_laptop";
            case "shoe":
                Shoe shoe = new Shoe();
                model.addAttribute("product", shoe);
                model.addAttribute("listGender", Arrays.stream(Gender.values()).collect(Collectors.toList()));
                return "products/product_form_shoe";
            case "book":
                Book book = new Book();
                book.setPublicationDate(LocalDateTime.now());
                model.addAttribute("product", book);
                return "products/product_form_book";
            case "clothes":
                Clothes clothes = new Clothes();
                model.addAttribute("listGender", Arrays.stream(Gender.values()).collect(Collectors.toList()));
                model.addAttribute("product", clothes);
                return "products/product_form_clothes";
            default:
                Product product = new Product();
                model.addAttribute("product", product);
                return "products/product_form";
        }
    }

    @PostMapping("/products/save")
    public String saveProduct(Product product, @RequestParam(name = "fileImage", required = false) MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ShopmeUserDetails userDetails = (ShopmeUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            product.setMainImage(fileName);

            Product savedProduct = productService.save(product, user);

            String uploadDir = "uploads/product-images/" + savedProduct.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        } else {
            productService.save(product, user);
        }

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return "redirect:/products";
    }

    @PostMapping("/laptop/save")
    public String saveLaptop(Laptop laptop, @RequestParam(name = "fileImage", required = false) MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ShopmeUserDetails userDetails = (ShopmeUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            laptop.setMainImage(fileName);

            Laptop savedLaptop = productService.saveLaptop(laptop, user);

            String uploadDir = "uploads/laptop-images/" + savedLaptop.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        } else {
            productService.saveLaptop(laptop, user);
        }

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return "redirect:/products";
    }

    @PostMapping("/clothes/save")
    public String saveClothes(Clothes clothes, @RequestParam(name = "fileImage", required = false) MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ShopmeUserDetails userDetails = (ShopmeUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            clothes.setMainImage(fileName);

            Clothes savedClothes = productService.saveClothes(clothes, user);

            String uploadDir = "uploads/laptop-images/" + savedClothes.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        } else {
            productService.saveClothes(clothes, user);
        }

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return "redirect:/products";
    }

    @PostMapping("/book/save")
    public String saveBook(Book book, @RequestParam(name = "fileImage", required = false) MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ShopmeUserDetails userDetails = (ShopmeUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            book.setMainImage(fileName);

            Book savedbook = productService.saveBook(book, user);

            String uploadDir = "uploads/laptop-images/" + savedbook.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        } else {
            productService.saveBook(book, user);
        }

        redirectAttributes.addFlashAttribute("message", "The product has been saved successfully.");
        return "redirect:/products";
    }

    @PostMapping("/shoe/save")
    public String saveShoe(Shoe shoe, @RequestParam(name = "fileImage", required = false) MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ShopmeUserDetails userDetails = (ShopmeUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();
        if (!file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            shoe.setMainImage(fileName);

            Shoe savedShoe = productService.saveShoe(shoe, user);

            String uploadDir = "uploads/laptop-images/" + savedShoe.getId();
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, file);
        } else {
            productService.saveShoe(shoe, user);
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
