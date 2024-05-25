package com.shopme.admin.product;

import com.shopme.common.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ProductService {
    @Autowired
    private ProductRepository productRepo;

    public static final int PRODUCTS_PER_PAGE = 8;

    public List<Product> listAll() {
        return (List<Product>) productRepo.findAll();
    }

    public Page<Product> listByPage(Integer pagenum, String keyword) {
        Pageable pageable = PageRequest.of(pagenum - 1, PRODUCTS_PER_PAGE);
        if (keyword != null && !keyword.isBlank()) {
            return productRepo.findAll(keyword, pageable);
        } else {
            return productRepo.findAll(pageable);
        }
    }

    public Product save(Product product) {
        if (product.getId() == null) {
            product.setCreatedTime(new Date());
        } else {
            Product productInDB = productRepo.findById(product.getId()).get();
            product.setCreatedTime(productInDB.getCreatedTime());
        }

        if (product.getAlias() == null || product.getAlias().isBlank()) {
            String defaultAlias = product.getName().replace(" ", "-");
            product.setAlias(defaultAlias);
        } else {
            product.setAlias(product.getAlias().replace(" ", "-"));
        }

        product.setCreatedTime(new Date());

        productRepo.save(product);
        return product;
    }

    public void updateProductEnabledStatus(Integer id, boolean enabled) {
        productRepo.updateEnabledStatus(id, enabled);
    }

    public void delete(Integer id) {
        productRepo.deleteById(id);
    }

    public Product findById(Integer id) {
        return productRepo.findById(id).get();
    }
}
