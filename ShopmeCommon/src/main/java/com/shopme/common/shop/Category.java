package com.shopme.common.shop;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 128, nullable = false, unique = true)
    private String name;

    @Column(length = 128, nullable = false, unique = true)
    private String alias;

    @Column(length = 128, nullable = false)
    private String image;

    private boolean enabled;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private Set<Category> children = new HashSet<>();

    public Category(String name) {
        this.name = name;
        this.alias = name;
    }

    public Category(Integer id) {
        this.id = id;
    }

    public Category(String name, Category parent) {
        this(name);
        this.parent = parent;
    }

    public Category() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

    public Set<Category> getChildren() {
        return children;
    }

    public void setChildren(Set<Category> children) {
        this.children = children;
    }

    @Transient
    public String getImagePath() {
        if (id == null || image.equals("default.png"))
            return "/images/default_thumbnail.png";
        return "/uploads/categories-images/" + this.id + "/" + this.image;
    }

}
