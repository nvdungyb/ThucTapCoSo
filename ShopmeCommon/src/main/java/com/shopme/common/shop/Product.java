package com.shopme.common.shop;

import com.shopme.common.entity.Seller;
import com.shopme.common.enums.ECurrency;
import com.shopme.common.enums.ProductType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "products")
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 256, nullable = false)
    private String name;

    @Column(unique = true, length = 256, nullable = false)
    private String alias;

    @Column(length = 512, nullable = false, name = "short_description")
    private String shortDescription;

    @Column(length = 4096, nullable = false, name = "full_description")
    private String fullDescription;

    @Column(name = "created_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @Column(name = "updated_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updatedTime;

    @Column(nullable = false)
    private boolean enabled;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private float price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ECurrency currency;

    @Column(nullable = false)
    private double rating;

    @Column(nullable = false)
    private float weight;

    @Column(name = "main_image", length = 256, nullable = false)
    private String mainImage;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @OneToMany(mappedBy = "product", cascade = CascadeType.PERSIST)
    private Set<ProductImage> images = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private ProductType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private Seller seller;

    public void addExtraImage(String imageName) {
        this.images.add(new ProductImage(imageName, this));
    }

    @Transient
    public String getMainImagePath() {
        if (this.id == null || this.mainImage == null) return "/images/default_thumbnail.png";
        return "/uploads/product-images/" + this.id + "/" + this.mainImage;
    }

    public Product(Long id) {
        this.id = id;
    }

    @Transient
    public String toString() {
        return this.name + " " + this.alias + " " + this.shortDescription + " " + this.fullDescription + " " + this.createdTime + " " + this.updatedTime + " " + this.enabled + " " + this.stockQuantity + " " + this.price + " " + this.currency + " " + this.rating + " " + this.weight + " " + this.mainImage + " " + this.category + " " + this.brand + " " + this.images + " " + this.seller;
    }

}
