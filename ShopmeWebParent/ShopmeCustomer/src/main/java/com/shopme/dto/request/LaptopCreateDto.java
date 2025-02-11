package com.shopme.dto.request;

import com.shopme.common.enums.ECurrency;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Date;

@Data
public class LaptopCreateDto {
    @NotBlank(message = "Laptop name is required")
    @Size(max = 256, message = "Laptop name must be at most 255 characters")
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

    @FutureOrPresent(message = "Updated time cannot be in the past")
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

    @NotNull(message = "Processor is required")
    private String processor;

    @NotNull(message = "Processor speed is required")
    private double processorSpeed;

    @NotNull(message = "RAM is required")
    private int ram;

    @NotNull(message = "RAM type is required")
    private String ramType;

    @NotNull(message = "Storage capacity is required")
    private Integer storageCapacity;

    @NotNull(message = "Storage type is required")
    private String storageType;

    @NotNull(message = "Screen size is required")
    private double screenSize;

    @NotNull(message = "Resolution is required")
    private String resolution;

    @NotNull(message = "Refresh rate is required")
    private Integer refeshRate;

    @NotNull(message = "CPU is required")
    private String cpu;

    @NotNull(message = "Battery capacity is required")
    private Integer batteryCapacity;

    @NotNull(message = "OS is required")
    private String os;

    @NotNull(message = "Category ID is required")
    @Min(value = 1, message = "Invalid category ID")
    private Long categoryId;

    @NotNull(message = "Seller ID is required")
    @Min(value = 1, message = "Invalid seller ID")
    private Long sellerId;

    public LaptopCreateDto() {
        this.createdTime = new Date();
        this.updatedTime = new Date();
        this.enabled = false;
        this.rating = 0;
    }
}
