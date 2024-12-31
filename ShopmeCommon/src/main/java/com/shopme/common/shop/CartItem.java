package com.shopme.common.shop;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "is_selected", nullable = false)
    private boolean isSelected;

    @Column(name = "created_at", nullable = false)
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_id")
    private ProductDiscount discount;

    @Transient
    public double getSubtotal() {
        return this.product.getPrice() * quantity;
    }
}
