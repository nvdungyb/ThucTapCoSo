package com.shopme.admin.category;

import com.shopme.common.entity.Category;
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

    public Page<Category> listByPage(int pageNum) {
        Pageable pageable = PageRequest.of(pageNum - 1, CATEGORIES_PER_PAGE);
        return categoryRepo.findAll(pageable);
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
}
