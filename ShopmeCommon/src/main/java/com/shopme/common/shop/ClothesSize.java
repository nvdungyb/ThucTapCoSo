package com.shopme.common.shop;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Entity
@Table(name = "clothes_size")
@Data
public class ClothesSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    @Min(30)
    @Max(45)
    private Integer size;

    @ManyToOne
    @JoinColumn(name = "clothes_id")
    private Clothes clothes;
}
