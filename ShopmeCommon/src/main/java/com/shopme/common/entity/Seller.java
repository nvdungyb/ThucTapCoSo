package com.shopme.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
public class Seller extends User {
    @Column(length = 10, name = "tax_id", nullable = false)
    private String taxId;

    @Column(length = 30, name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "shop_rating", nullable = false)
    private double shopRating;

    @Column(name = "number_of_orders", nullable = false)
    private Integer numberOfOrders;
}
