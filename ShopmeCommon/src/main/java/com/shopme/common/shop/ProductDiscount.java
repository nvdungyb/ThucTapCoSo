package com.shopme.common.shop;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "product_discount")
@Data
public class ProductDiscount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "discount_percentage", nullable = false)
    private double discountPercentage;

    @Column(length = 100, nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "start_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endDate;
}
