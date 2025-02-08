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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.cert.CollectionCertStoreParameters;
import java.util.Collection;
import java.util.Collections;
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
        return productRepository.findById(productId)
                .map(product -> {
                    if (!product.isEnabled()) {
                        logger.warn("Product {} is not enabled", productId);
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
                    }
                    // todo: if want to extend the functionality, add more logic here
                    return product;
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public Product getDetailProductForSeller(Long productId, Long sellerId) {
        return productRepository.findById(productId)
                .map(product -> {
                    if (!product.getSeller().getId().equals(sellerId)) {
                        logger.warn("Seller ID {} tried to access product {} that does not belong to them.", sellerId, productId);
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
                    }
                    // todo: if want to extend the functionality, add more logic here
                    return product;
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public List<Product> getProductsByCategory(Long id) {
        productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));

        return productRepository.findAllByCategory_Id(id);
    }

    public List<Product> searchProductsByKey(String key) {
        if (key == null || key.isEmpty()) {
            return Collections.emptyList();
        }
        return productRepository.findProductsByKey(key);
    }

    public List<Product> filterProducts(Long categoryId, Double minPrice, Double maxPrice) {
        return productRepository.filterProducts(categoryId, minPrice, maxPrice);
    }
}
