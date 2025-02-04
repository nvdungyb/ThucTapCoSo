package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Entity
@Data
@DiscriminatorValue("Seller")
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private User user;

    @Column(length = 10, name = "tax_id", nullable = false)
    private String taxId;

    @Column(length = 30, name = "shop_name", nullable = false)
    private String shopName;

    @Column(name = "shop_rating", nullable = false)
    private double shopRating;

    @Column(name = "number_of_orders", nullable = false)
    private Integer numberOfOrders;

    public String toString() {
        return this.user.getFirstName() + " " + this.user.getLastName() + " - " + this.shopName;
    }
}
