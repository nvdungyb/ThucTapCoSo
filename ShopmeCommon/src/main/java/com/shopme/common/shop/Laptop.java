package com.shopme.common.shop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

@Entity
@Data
public class Laptop extends Product {
    @Column(length = 10, nullable = false)
    private String processor;

    @Column(nullable = false)
    private double processorSpeed;

    @Column(nullable = false)
    private int ram;

    @Column(name = "ram_type", length = 10, nullable = false)
    private String ramType;

    @Column(name = "storage_capacity", nullable = false)
    private Integer storageCapacity;

    @Column(name = "storage_type", length = 10, nullable = false)
    private String storageType;

    @Column(name = "screen_size", nullable = false)
    private double screenSize;

    @Column(name = "resolution", length = 10, nullable = false)
    private String resolution;

    @Column(name = "refresh_rate", nullable = false)
    private Integer refeshRate;

    @Column(length = 10, nullable = false)
    private String cpu;

    @Column(name = "battery_capacity", nullable = false)
    private Integer batteryCapacity;

    @Column(length = 10, nullable = false)
    private String os;
}
