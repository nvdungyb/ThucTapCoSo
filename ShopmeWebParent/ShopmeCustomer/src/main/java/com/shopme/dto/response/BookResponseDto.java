package com.shopme.dto.response;

import com.shopme.common.enums.ECurrency;
import com.shopme.common.shop.Book;
import com.shopme.common.shop.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookResponseDto implements ProductResponseDto {
    private Long id;
    private String name;
    private String alias;
    private String shortDescription;
    private String fullDescription;
    private Date createdTime;
    private Date updatedTime;
    private boolean enabled;
    private Integer stockQuantity;
    private float price;
    private ECurrency currency;
    private double rating;
    private float weight;
    private String mainImage;
    private Long categoryId;
    private Set<ProductImage> images;
    private Long sellerId;
    private String author;
    private String publisher;
    private String isbn;
    private Date publicationDate;
    private Integer pageCount;

    public BookResponseDto(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        this.alias = book.getAlias();
        this.shortDescription = book.getShortDescription();
        this.fullDescription = book.getFullDescription();
        this.createdTime = book.getCreatedTime();
        this.updatedTime = book.getUpdatedTime();
        this.enabled = book.isEnabled();
        this.stockQuantity = book.getStockQuantity();
        this.price = book.getPrice();
        this.currency = book.getCurrency();
        this.rating = book.getRating();
        this.weight = book.getWeight();
        this.mainImage = book.getMainImage();
        this.categoryId = book.getCategory().getId();
        this.images = book.getImages();
        this.sellerId = book.getSeller().getId();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.isbn = book.getIsbn();
        this.publicationDate = book.getPublicationDate();
        this.pageCount = book.getPageCount();
    }
}
