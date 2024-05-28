package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @Column(name = "order_time")
    private Date orderTime;

    @Column(name = "shipping_cost")
    private float shippingCost;

    @Column(name = "product_cost")
    private float productCost;

    private float total;

    @Column(name = "order_status")
    private boolean orderStatus;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

}
