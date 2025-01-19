package com.shopme.common.entity;

import com.shopme.common.shop.Cart;
import jakarta.persistence.*;
import lombok.*;

import static jakarta.persistence.CascadeType.ALL;

@Entity
@Getter
@Builder
@Table(name = "customers")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(callSuper = true, exclude = "cart")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = ALL, fetch = FetchType.LAZY)
    private User user;

    @Column(name = "loyalty_points", nullable = false)
    private int loyaltyPoints;

    @Column(name = "total_spent", nullable = false)
    private double totalSpent;

    public Customer(long customerId) {
        this.id = customerId;
    }

    public String getFullName() {
        return this.user.getFirstName() + " " + this.user.getLastName();
    }

    @OneToOne(mappedBy = "customer", cascade = ALL, fetch = FetchType.LAZY)
    private Cart cart;
}
