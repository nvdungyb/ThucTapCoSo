package com.shopme.dto.request;

import com.shopme.common.enums.ECurrency;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class LaptopUpdateDto {
    @NotNull(message = "Book ID cannot be null")
    @Min(value = 1, message = "Invalid book ID")
    private Long id;

    private String name;
    private String shortDescription;
    private String fullDescription;
    private Date updatedTime;
    private boolean enabled;
    private Integer stockQuantity;
    private Float price;
    private ECurrency currency;

    @Min(value = 0, message = "Rating cannot be negative")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Double rating;

    @DecimalMin(value = "0.1", message = "Weight must be at least 0.1")
    private Float weight;

    @Size(max = 255, message = "Main image must be at most 255 characters")
    private String mainImage;

    private String processor;

    private Double processorSpeed;

    private Integer ram;

    private String ramType;

    private Integer storageCapacity;

    private String storageType;

    private Double screenSize;
    private String resolution;
    private Integer refeshRate;
    private String cpu;
    private Integer batteryCapacity;
    private String os;

    @Min(value = 1, message = "Invalid category ID")
    private Long categoryId;

    public LaptopUpdateDto() {
        this.updatedTime = new Date();
        this.enabled = false;
        this.rating = 0d;
    }
}
