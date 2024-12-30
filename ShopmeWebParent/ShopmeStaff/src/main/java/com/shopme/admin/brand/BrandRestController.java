package com.shopme.admin.brand;

import com.shopme.admin.category.CategoryDTO;
import com.shopme.common.shop.Brand;
import com.shopme.common.shop.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
public class BrandRestController {
    @Autowired
    BrandService brandService;

    @PostMapping("/brands/check_unique")
    public String checkUnique(Integer id, String name) {
        return brandService.checkUnique(id, name) ? "OK" : "Duplicated";
    }

    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> listCategoriesByBrand(@PathVariable(name = "id") Integer brandId) throws BrandNotFoundException {
        List<CategoryDTO> listCategoryDTO = new ArrayList<>();
        try {
            Brand brand = brandService.get(brandId);
            Set<Category> listCategories = brand.getCategories();
            for (Category category : listCategories) {
                CategoryDTO categoryDTO = new CategoryDTO(category.getId(), category.getName());
                listCategoryDTO.add(categoryDTO);
            }
            return listCategoryDTO;
        } catch (Exception e) {
            throw new BrandNotFoundException("Could not find any brand with ID " + brandId);
        }
    }
}
