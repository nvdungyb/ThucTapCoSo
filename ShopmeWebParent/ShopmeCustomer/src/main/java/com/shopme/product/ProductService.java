package com.shopme.product;

import com.shopme.common.shop.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 10;

    @Autowired
    private ProductRepository productRepository;

    public Page<Product> listByCategory(int pageNum, Integer categoryId) {
        String categoryIdMatch = "-" + String.valueOf(categoryId) + "-";
        Pageable pageable = PageRequest.of(pageNum - 1, PRODUCTS_PER_PAGE);
        return productRepository.listByCategory(categoryId, categoryIdMatch, pageable);
    }

    public Product getProduct(String alias) throws Exception {
        if (alias == null || alias.isEmpty())
            throw new Exception("Alias cannot be null or empty");

        return productRepository.findByAlias(alias);
    }

    public List<Product> findAll() {
        return (List<Product>) productRepository.findAll();
    }
}
