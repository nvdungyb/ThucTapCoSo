package com.shopme.common.shop;

import com.shopme.common.entity.Customer;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@EqualsAndHashCode(callSuper = false, exclude = {"customer", "cartItems"})
@ToString(callSuper = true, exclude = "cartItems")
@Table(name = "carts")
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /* đây là giá trị dẫn xuất, không lưu vào db
    private double totalPrice;
    private double discountAmount;
    private double finalPrice;
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "is_active", nullable = false)
    private boolean isActice;

    @Column(name = "create_at", nullable = false)
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date createAt;

    @Column(name = "update_at", nullable = false)
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date updateAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();
}
