package com.shopme.common.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 40, nullable = false)
    private String province;

    @Column(length = 40, nullable = false)
    private String district;

    @Column(length = 40, nullable = false)
    private String town;

    @Column(length = 40, nullable = false)
    private String details;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}