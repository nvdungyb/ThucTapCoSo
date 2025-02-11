package com.shopme.mapper;

import com.shopme.common.entity.Seller;
import com.shopme.common.shop.Category;
import com.shopme.common.shop.Laptop;
import com.shopme.dto.request.LaptopCreateDto;
import com.shopme.dto.request.LaptopUpdateDto;
import jakarta.validation.Valid;
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

    public void updateFromDto(@Valid LaptopUpdateDto laptopUpdateDto, Laptop laptop, Category category) {
        laptop.setName(laptopUpdateDto.getName() != null ? laptopUpdateDto.getName() : laptop.getName());
        laptop.setShortDescription(laptopUpdateDto.getShortDescription() != null ? laptopUpdateDto.getShortDescription() : laptop.getShortDescription());
        laptop.setFullDescription(laptopUpdateDto.getFullDescription() != null ? laptopUpdateDto.getFullDescription() : laptop.getFullDescription());
        laptop.setUpdatedTime(laptopUpdateDto.getUpdatedTime() != null ? laptopUpdateDto.getUpdatedTime() : laptop.getUpdatedTime());
        laptop.setEnabled(laptopUpdateDto.isEnabled());
        laptop.setStockQuantity(laptopUpdateDto.getStockQuantity() != null ? laptopUpdateDto.getStockQuantity() : laptop.getStockQuantity());
        laptop.setPrice(laptopUpdateDto.getPrice() != null ? laptopUpdateDto.getPrice() : laptop.getPrice());
        laptop.setCurrency(laptopUpdateDto.getCurrency() != null ? laptopUpdateDto.getCurrency() : laptop.getCurrency());
        laptop.setRating(laptopUpdateDto.getRating() != null ? laptopUpdateDto.getRating() : laptop.getRating());
        laptop.setWeight(laptopUpdateDto.getWeight() != null ? laptopUpdateDto.getWeight() : laptop.getWeight());
        laptop.setMainImage(laptopUpdateDto.getMainImage() != null ? laptopUpdateDto.getMainImage() : laptop.getMainImage());
        laptop.setCategory(category);
        laptop.setProcessor(laptopUpdateDto.getProcessor() != null ? laptopUpdateDto.getProcessor() : laptop.getProcessor());
        laptop.setProcessorSpeed(laptopUpdateDto.getProcessorSpeed() != null ? laptopUpdateDto.getProcessorSpeed() : laptop.getProcessorSpeed());
        laptop.setRam(laptopUpdateDto.getRam() != null ? laptopUpdateDto.getRam() : laptop.getRam());
        laptop.setRamType(laptopUpdateDto.getRamType() != null ? laptopUpdateDto.getRamType() : laptop.getRamType());
        laptop.setStorageCapacity(laptopUpdateDto.getStorageCapacity() != null ? laptopUpdateDto.getStorageCapacity() : laptop.getStorageCapacity());
        laptop.setStorageType(laptopUpdateDto.getStorageType() != null ? laptopUpdateDto.getStorageType() : laptop.getStorageType());
        laptop.setScreenSize(laptopUpdateDto.getScreenSize() != null ? laptopUpdateDto.getScreenSize() : laptop.getScreenSize());
        laptop.setResolution(laptopUpdateDto.getResolution() != null ? laptopUpdateDto.getResolution() : laptop.getResolution());
        laptop.setRefeshRate(laptopUpdateDto.getRefeshRate() != null ? laptopUpdateDto.getRefeshRate() : laptop.getRefeshRate());
        laptop.setCpu(laptopUpdateDto.getCpu() != null ? laptopUpdateDto.getCpu() : laptop.getCpu());
        laptop.setBatteryCapacity(laptopUpdateDto.getBatteryCapacity() != null ? laptopUpdateDto.getBatteryCapacity() : laptop.getBatteryCapacity());
        laptop.setOs(laptopUpdateDto.getOs() != null ? laptopUpdateDto.getOs() : laptop.getOs());
    }
}
