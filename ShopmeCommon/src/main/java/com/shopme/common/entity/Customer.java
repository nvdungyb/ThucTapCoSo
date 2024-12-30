package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "customers")
@DiscriminatorValue("Customer")
@NoArgsConstructor
public class Customer extends User {
    @Column(name = "loyalty_points", nullable = false)
    private int loyaltyPoints;

    @Column(name = "total_spent", nullable = false)
    private double totalSpent;

    public String getFullName() {
        return this.getFirstName() + " " + this.getLastName();
    }
}
