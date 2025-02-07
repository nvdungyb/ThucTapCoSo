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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductService {
    public static final int PRODUCTS_PER_PAGE = 10;

    private final ProductRepository productRepository;
    private final CategoryReposistory categoryReposistory;
    private final SellerReposistory sellerReposistory;
    private final BookReposistory bookReposistory;
    private final BookMapper bookMapper;
    private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(ProductService.class);

    public ProductService(ProductRepository productRepository, CategoryReposistory categoryReposistory, SellerReposistory sellerReposistory, BookReposistory bookReposistory, BookMapper bookMapper) {
        this.productRepository = productRepository;
        this.categoryReposistory = categoryReposistory;
        this.sellerReposistory = sellerReposistory;
        this.bookReposistory = bookReposistory;
        this.bookMapper = bookMapper;
    }

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

    public Product getDetailProductForCustomer(Long productId) {
        return productRepository.findProductByIdAndEnabled(productId, true)
                .orElseThrow(() -> {
                    logger.warn("Product not found or disabled, ID: {}", productId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found or disabled");
                });
    }

    public Product getDetailProductForStaff(Long productId) {
        return productRepository.findProductById(productId)
                .orElseThrow(() -> {
                    logger.warn("Product not found, ID: {}", productId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
                });
    }
}
