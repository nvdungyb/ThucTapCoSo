package com.shopme.common.shop;

import com.shopme.common.utils.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, exclude = "sizeList")
@ToString(callSuper = true, exclude = "sizeList")
public class Clothes extends Product {
    @Enumerated(EnumType.STRING)
    @Column(length = 10, nullable = false)
    private Gender gender;

    @Column(length = 10, nullable = false)
    private String color;

    @Column(length = 30, nullable = false)
    private String material;

    @OneToMany(mappedBy = "clothes", fetch = FetchType.EAGER)
    private List<ClothesSize> sizeList = new ArrayList<>();
}
