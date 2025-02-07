package com.shopme.dto.request;

import com.shopme.common.enums.ECurrency;
import lombok.Data;

import java.util.Date;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class BookCreateDto {
    @NotBlank(message = "Book name is required")
    @Size(max = 256, message = "Book name must be at most 255 characters")
    private String name;

    @Size(max = 256, message = "Alias must be at most 255 characters")
    private String alias;

    @Size(max = 512, message = "Short description must be at most 512 characters")
    private String shortDescription;

    @NotBlank(message = "Full description is required")
    @Size(max = 4096, message = "Full description must be at most 4096 characters")
    private String fullDescription;

    @PastOrPresent(message = "Created time cannot be in the future")
    private Date createdTime;

    @FutureOrPresent(message = "Updated time cannot be in the future")
    private Date updatedTime;

    private boolean enabled;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Float price;

    @NotNull(message = "Currency is required")
    private ECurrency currency;

    @Min(value = 0, message = "Rating cannot be negative")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private double rating;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1")
    private Float weight;

    @Size(max = 255, message = "Main image must be at most 255 characters")
    private String mainImage;

    @NotBlank(message = "Author is required")
    @Size(max = 256, message = "Author name must be at most 256 characters")
    private String author;

    @NotBlank(message = "Publisher is required")
    @Size(max = 255, message = "Publisher name must be at most 255 characters")
    private String publisher;

    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "\\d{10}", message = "ISBN must be 10")
    private String isbn;

    @NotNull(message = "Publication date is required")
    @PastOrPresent(message = "Publication date cannot be in the future")
    private Date publicationDate;

    private String format;

    @NotNull(message = "Page count is required")
    @Min(value = 1, message = "Page count must be at least 1")
    private Integer pageCount;

    @NotNull(message = "Category ID is required")
    @Min(value = 1, message = "Invalid category ID")
    private Long categoryId;

    @NotNull(message = "Seller ID is required")
    @Min(value = 1, message = "Invalid seller ID")
    private Long sellerId;

    public BookCreateDto() {
        this.createdTime = new Date();
        this.updatedTime = new Date();
        this.enabled = false;
        this.rating = 0;
        this.publicationDate = new Date();
    }
}
