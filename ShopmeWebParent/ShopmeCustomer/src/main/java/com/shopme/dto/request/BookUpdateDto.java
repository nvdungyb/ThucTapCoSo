package com.shopme.dto.request;

import com.shopme.common.enums.ECurrency;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class BookUpdateDto {
    @NotNull(message = "Book ID cannot be null")
    @Min(value = 1, message = "Invalid book ID")
    private Long id;

    @Size(max = 256, message = "Book name must be at most 255 characters")
    private String name;

    @Size(max = 512, message = "Short description must be at most 512 characters")
    private String shortDescription;

    @Size(max = 4096, message = "Full description must be at most 4096 characters")
    private String fullDescription;

    private Date updatedTime;

    private Boolean enabled;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private Float price;

    private ECurrency currency;

    @Min(value = 0, message = "Rating cannot be negative")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Double rating;

    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1")
    private Float weight;

    @Size(max = 255, message = "Main image must be at most 255 characters")
    private String mainImage;

    @Size(max = 256, message = "Author name must be at most 256 characters")
    private String author;

    @Size(max = 255, message = "Publisher name must be at most 255 characters")
    private String publisher;

    @PastOrPresent(message = "Publication date cannot be in the future")
    private Date publicationDate;

    private String format;

    @Min(value = 1, message = "Page count must be at least 1")
    private Integer pageCount;

    @Min(value = 1, message = "Invalid category ID")
    private Long categoryId;

    public BookUpdateDto() {
        this.updatedTime = new Date();
    }
}
