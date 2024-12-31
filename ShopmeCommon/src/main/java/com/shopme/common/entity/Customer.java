package com.shopme.common.entity;

import com.shopme.common.shop.Cart;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@Table(name = "customers")
@DiscriminatorValue("Customer")
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(callSuper = true, exclude = "cart")
public class Customer extends User {
    @Column(name = "loyalty_points", nullable = false)
    private int loyaltyPoints;

    @Column(name = "total_spent", nullable = false)
    private double totalSpent;

    public String getFullName() {
        return this.getFirstName() + " " + this.getLastName();
    }

    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart;
}
