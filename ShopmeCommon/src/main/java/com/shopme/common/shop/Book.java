package com.shopme.common.shop;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Entity
@Data
@Table(name = "books")
public class Book extends Product {
    private String author;

    private String publisher;

    @Column(length = 10, nullable = false)
    private String isbn;

    @Column(name = "publication_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publicationDate;

    @Column(name = "page_count", nullable = false)
    private Integer pageCount;
}
