package com.shopme.admin.category;

import com.shopme.common.entity.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import java.util.List;
import java.util.Set;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CategoryRepositoryTests {

    @Autowired
    private CategoryRepository repo;

    @Test
    public void testCreateRootCategory() {
        Category category = new Category("Computers");
        Category savedCategory = repo.save(category);

        assertThat(savedCategory.getId()).isGreaterThan(0);
    }

    @Test
    public void testCreateSubCategory() {
        Category parent = new Category(22);
        Category memory = new Category("Memory", parent);

        repo.save(memory);
        assertThat(memory.getId()).isGreaterThan(0);
    }

    @Test
    public void setParentCategory() {
        Category parent = new Category(21);
        Category child = repo.findById(2).get();
        child.setParent(parent);

        repo.save(child);
        assertThat(child.getParent().getId()).isEqualTo(parent.getId());
    }

    @Test
    public void testGetCategory() {
        Category category = repo.findById(1).get();
        System.out.println(category.getName());

        Set<Category> children = category.getChildren();
        for (Category subCategory : children) {
            System.out.println(subCategory.getName());
        }

        assertThat(children.size()).isGreaterThan(0);
    }

    @Test
    public void testPrintCategoryHierarchy() {
        Iterable<Category> categories = repo.findAll();
        for (Category category : categories) {
            if (category.getParent() == null) {
                System.out.println(category.getName());
                Set<Category> children = category.getChildren();
                for (Category subCategory : children) {
                    System.out.println("--" + subCategory.getName());
                    printChildren(subCategory, 2);
                }
            }
        }
    }

    private void printChildren(Category parent, Integer subLevel) {
        for (Category subCategory : parent.getChildren()) {
            for (int i = 1; i <= subLevel; i++) {
                System.out.print("--");
            }
            System.out.println(subCategory.getName());
            printChildren(subCategory, subLevel + 1);
        }
    }
}

