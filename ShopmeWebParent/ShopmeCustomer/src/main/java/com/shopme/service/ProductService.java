package com.shopme.service;

import com.shopme.Reposistory.*;
import com.shopme.common.entity.Seller;
import com.shopme.common.shop.Book;
import com.shopme.common.shop.Category;
import com.shopme.common.shop.Laptop;
import com.shopme.common.shop.Product;
import com.shopme.dto.request.BookCreateDto;
import com.shopme.dto.request.BookUpdateDto;
import com.shopme.dto.request.LaptopCreateDto;
import com.shopme.mapper.BookMapper;
import com.shopme.mapper.LaptopMapper;
import jakarta.validation.Valid;
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
    private final LaptopReposistory laptopReposistory;
    private final LaptopMapper laptopMapper;

    public ProductService(ProductRepository productRepository, CategoryReposistory categoryReposistory, SellerReposistory sellerReposistory, BookReposistory bookReposistory, BookMapper bookMapper, LaptopReposistory laptopReposistory, LaptopMapper laptopMapper) {
        this.productRepository = productRepository;
        this.categoryReposistory = categoryReposistory;
        this.sellerReposistory = sellerReposistory;
        this.bookReposistory = bookReposistory;
        this.bookMapper = bookMapper;
        this.laptopReposistory = laptopReposistory;
        this.laptopMapper = laptopMapper;
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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));

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
        List<Product> products = productRepository.findAllByCategory_Id(id);

        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No products found for this category");
        }

        return products;
    }

    public List<Product> searchProductsByKey(String key) {
        return null;
    }

    public List<Product> filterProducts(Long categoryId, Double minPrice, Double maxPrice) {
        return productRepository.filterProducts(categoryId, minPrice, maxPrice);
    }

    public Book updateBook(@Valid BookUpdateDto bookUpdateDto, Long userId) {
        Book book = bookReposistory.findById(bookUpdateDto.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found"));

        if (!book.getSeller().getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        Category category = categoryReposistory.findById(bookUpdateDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        bookMapper.updateFromDto(bookUpdateDto, book, category);
        return bookReposistory.save(book);
    }

    public void deleteProduct(Long id, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "Product not found"));

        if (!product.getSeller().getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        productRepository.deleteById(id);
    }

    public List<Product> getProductsBySeller(Long userId) {
        List<Product> products = productRepository.findProductsBySellerUserId(userId);

        if (products.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No products found for this seller");
        }

        return products;
    }

    @Transactional
    public Laptop createLaptop(@Valid LaptopCreateDto laptopCreateDto, Long userId) {
        if (laptopReposistory.existsByAlias(laptopCreateDto.getAlias())) {
            throw new IllegalArgumentException("Alias is already used by another laptop");
        }

        Category category = categoryReposistory.findById(laptopCreateDto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        Seller seller = sellerReposistory.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied"));

        Laptop laptop = laptopMapper.toEntity(laptopCreateDto, category, seller);
        return laptopReposistory.save(laptop);
    }
}
