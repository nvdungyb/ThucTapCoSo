package com.shopme.category;

import com.shopme.common.shop.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> listNoChildrenCategories() {
        List<Category> listCategories = categoryRepository.findAllEnabled();
        List<Category> listNoChildrenCategories = new ArrayList<>();

        listCategories.forEach(cat -> {
            if (cat.getChildren().size() == 0 || cat.getChildren() == null) {
                listNoChildrenCategories.add(cat);
            }
        });

        return listNoChildrenCategories;
    }

    public Category getCategory(String alias) {
        return categoryRepository.findByAliasEnabled(alias);
    }

    public List<Category> listCategoryParents(Category child) {
        List<Category> listParents = new ArrayList<>();
        Category parent = child.getParent();

        while (parent != null) {
            listParents.add(0, parent);
            parent = parent.getParent();
        }

        listParents.add(child);

        return listParents;
    }
}
