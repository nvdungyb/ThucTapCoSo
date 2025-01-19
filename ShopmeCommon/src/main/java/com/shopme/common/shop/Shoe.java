package com.shopme.common.shop;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(callSuper = true, exclude = "sizeList")
public class Shoe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Product product;

    @Column(length = 10, nullable = false)
    private String color;

    @Column(length = 10, nullable = false)
    private String material;

    @Column(length = 10, nullable = false)
    private String gender;

    @OneToMany(mappedBy = "shoe", fetch = FetchType.EAGER)
    private List<ShoeSize> sizeList = new ArrayList<>();
}
