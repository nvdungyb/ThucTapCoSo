package com.shopme.common.shop;

import com.shopme.common.enums.EGender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(callSuper = true, exclude = "sizeList")
@DiscriminatorValue("Clothes")
public class Clothes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Product product;

    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private EGender gender;

    @Column(length = 10, nullable = false)
    private String color;

    @Column(length = 30, nullable = false)
    private String material;

    @OneToMany(mappedBy = "clothes", fetch = FetchType.LAZY)
    private List<ClothesSize> sizeList = new ArrayList<>();
}
