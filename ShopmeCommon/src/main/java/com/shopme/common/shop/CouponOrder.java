package com.shopme.common.shop;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
public class CouponOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "discount_percentage", nullable = false)
    private double discountPercentage;

    @Column(name = "upplied_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uppliedDate;

    @Column(name = "expiry_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expiryDate;

    private String status;

    @Column(nullable = false)
    private Integer quantity;
}
