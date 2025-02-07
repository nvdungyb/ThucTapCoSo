package com.shopme.service;

import com.shopme.Reposistory.BookReposistory;
import com.shopme.Reposistory.CategoryReposistory;
import com.shopme.Reposistory.ProductRepository;
import com.shopme.Reposistory.SellerReposistory;
import com.shopme.common.entity.Seller;
import com.shopme.common.shop.Book;
import com.shopme.common.shop.Category;
import com.shopme.common.shop.Product;
import com.shopme.dto.request.BookCreateDto;
import com.shopme.mapper.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 10;

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryReposistory categoryReposistory;
    @Autowired
    private SellerReposistory sellerReposistory;
    @Autowired
    private BookReposistory bookReposistory;
    @Autowired
    private BookMapper bookMapper;

    public Page<Product> listByCategory(int pageNum, Long categoryId) {
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

    @Transactional
    public Book createBook(BookCreateDto bookCreateDto, Long sellerId) {
        if (bookReposistory.existsByIsbn(bookCreateDto.getIsbn())) {
            throw new IllegalArgumentException("ISBN is already used by another book");
        }

        Category category = categoryReposistory.findById(bookCreateDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Seller seller = sellerReposistory.findByUserId(sellerId)
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));

        Book book = bookMapper.toEntity(bookCreateDto, category, seller);
        return bookReposistory.save(book);
    }
}
