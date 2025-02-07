package com.shopme.common.shop;

import com.shopme.common.enums.ECurrency;
import com.shopme.common.enums.EPaymentMethod;
import com.shopme.common.enums.EPaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Table(name = "payment")
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private EPaymentMethod paymentMethod;

    @Column(nullable = false)
    private double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ECurrency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private EPaymentStatus paymentStatus;

    @Column(name = "payment_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date paymentDate;

    @Column(name = "created_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDate;

    @Column(name = "updated_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedDate;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id")
    private Order order;
}
