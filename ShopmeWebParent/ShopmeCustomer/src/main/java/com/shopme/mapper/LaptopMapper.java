package com.shopme.mapper;

import com.shopme.common.entity.Seller;
import com.shopme.common.shop.Category;
import com.shopme.common.shop.Laptop;
import com.shopme.dto.request.LaptopCreateDto;
import org.springframework.stereotype.Component;

@Component
public class LaptopMapper {
    public Laptop toEntity(LaptopCreateDto laptopCreateDto, Category category, Seller seller) {
        Laptop laptop = Laptop.builder()
                .name(laptopCreateDto.getName())
                .alias(laptopCreateDto.getAlias())
                .shortDescription(laptopCreateDto.getShortDescription())
                .fullDescription(laptopCreateDto.getFullDescription())
                .createdTime(laptopCreateDto.getCreatedTime())
                .updatedTime(laptopCreateDto.getUpdatedTime())
                .enabled(laptopCreateDto.isEnabled())
                .stockQuantity(laptopCreateDto.getStockQuantity())
                .price(laptopCreateDto.getPrice())
                .currency(laptopCreateDto.getCurrency())
                .rating(laptopCreateDto.getRating())
                .weight(laptopCreateDto.getWeight())
                .mainImage(laptopCreateDto.getMainImage())
                .category(category)
                .seller(seller)
                .processor(laptopCreateDto.getProcessor())
                .processorSpeed(laptopCreateDto.getProcessorSpeed())
                .ram(laptopCreateDto.getRam())
                .ramType(laptopCreateDto.getRamType())
                .storageCapacity(laptopCreateDto.getStorageCapacity())
                .storageType(laptopCreateDto.getStorageType())
                .screenSize(laptopCreateDto.getScreenSize())
                .resolution(laptopCreateDto.getResolution())
                .refeshRate(laptopCreateDto.getRefeshRate())
                .cpu(laptopCreateDto.getCpu())
                .batteryCapacity(laptopCreateDto.getBatteryCapacity())
                .os(laptopCreateDto.getOs())
                .build();
        return laptop;
    }
}
