package com.shopme.dto.response;

import com.shopme.common.enums.ECurrency;
import com.shopme.common.shop.Laptop;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaptopEntityResponse implements ProductResponseDto {
    private String name;
    private String alias;
    private String shortDescription;
    private String fullDescription;
    private Date createdTime;
    private Date updatedTime;
    private boolean enabled;
    private Integer stockQuantity;
    private Float price;
    private ECurrency currency;
    private double rating;
    private Float weight;
    private String mainImage;
    private String processor;
    private double processorSpeed;
    private int ram;
    private String ramType;
    private Integer storageCapacity;
    private String storageType;
    private double screenSize;
    private String resolution;
    private Integer refeshRate;
    private String cpu;
    private Integer batteryCapacity;
    private String os;
    private Long categoryId;
    private Long sellerId;

    public LaptopEntityResponse(Laptop laptop) {
        this.name = laptop.getName();
        this.alias = laptop.getAlias();
        this.shortDescription = laptop.getShortDescription();
        this.fullDescription = laptop.getFullDescription();
        this.createdTime = laptop.getCreatedTime();
        this.updatedTime = laptop.getUpdatedTime();
        this.enabled = laptop.isEnabled();
        this.stockQuantity = laptop.getStockQuantity();
        this.price = laptop.getPrice();
        this.currency = laptop.getCurrency();
        this.rating = laptop.getRating();
        this.weight = laptop.getWeight();
        this.mainImage = laptop.getMainImage();
        this.processor = laptop.getProcessor();
        this.processorSpeed = laptop.getProcessorSpeed();
        this.ram = laptop.getRam();
        this.ramType = laptop.getRamType();
        this.storageCapacity = laptop.getStorageCapacity();
        this.storageType = laptop.getStorageType();
        this.screenSize = laptop.getScreenSize();
        this.resolution = laptop.getResolution();
        this.refeshRate = laptop.getRefeshRate();
        this.cpu = laptop.getCpu();
        this.batteryCapacity = laptop.getBatteryCapacity();
        this.os = laptop.getOs();
        this.categoryId = laptop.getCategory().getId();
        this.sellerId = laptop.getSeller().getId();
    }
}
