package com.shopme.common.shop;

import com.shopme.common.entity.Customer;
import com.shopme.common.utils.DeliveryStatus;
import com.shopme.common.utils.OrderStatus;
import com.shopme.common.utils.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name = "orders")
@EqualsAndHashCode(callSuper = false, exclude = {"orderItems", "customer"})
@ToString(callSuper = true, exclude = "cartItems")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    @Column(name = "order_time")
    private Date orderTime;

    @Column(name = "shipping_cost")
    private float shippingCost;

    @Column(name = "product_prices")
    private float productPrices;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false)
    private DeliveryStatus deliveryStatus;

    @Column(name = "update_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private Date updateDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private CouponOrder couponOrder;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Transient
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderTime=" + orderTime +
                ", shippingCost=" + shippingCost +
                ", productPrices=" + productPrices +
                ", orderStatus=" + orderStatus +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", customer=" + customer +
                ", deliveryStatus=" + deliveryStatus +
                ", updateDate=" + updateDate +
                ", paymentMethod=" + paymentMethod +
                ", couponOrder=" + couponOrder +
                ", orderItemsSize=" + orderItems.size() +
                '}';
    }
}
