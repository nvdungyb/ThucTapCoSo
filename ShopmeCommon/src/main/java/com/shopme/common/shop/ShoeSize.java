package com.shopme.common.shop;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "shoe_size")
@Data
public class ShoeSize {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer size;

    @ManyToOne
    @JoinColumn(name = "shoe_id")
    private Shoe shoe;
}
