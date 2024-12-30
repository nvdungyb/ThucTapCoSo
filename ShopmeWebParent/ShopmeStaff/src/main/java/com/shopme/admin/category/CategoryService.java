package com.shopme.admin.category;

import com.shopme.common.shop.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional                         // Không có annotation này thì sẽ không thực hiện được update.
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepo;

    public static final int CATEGORIES_PER_PAGE = 8;

    public List<Category> findAll() {
        return (List<Category>) categoryRepo.findAll();
    }

    public Page<Category> listByPage(int pageNum, String keyword) {
        Pageable pageable = PageRequest.of(pageNum - 1, CATEGORIES_PER_PAGE);
        if (keyword != null && !keyword.isEmpty()) {
            return categoryRepo.searchKeyword(keyword, pageable);
        } else {
            return categoryRepo.findAll(pageable);
        }
    }

    public void updateCategoryEnabledStatus(Integer id, boolean enabled) {
        categoryRepo.updateEnabledStatus(id, enabled);
    }

    public List<Category> listCategoriesUsedInForm() {
        return (List<Category>) categoryRepo.findAll();
    }

    public Category saveCategory(Category category) {
        return categoryRepo.save(category);
    }

    public Category get(Integer id) {
        return categoryRepo.getById(id);
    }

    public void delete(Integer id) {
        categoryRepo.deleteById(id);
    }

    public boolean isUnique(String name, String alias) {
        Category categoryByName = categoryRepo.findByName(name);
        Category categoryByAlias = categoryRepo.findByAlias(alias);
        if (categoryByName != null || categoryByAlias != null) {
            return false;
        }

        return true;
    }
}
