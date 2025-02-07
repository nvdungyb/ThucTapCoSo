package com.shopme.common.shop;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Product product;

    private String author;

    private String publisher;

    @Column(length = 10, nullable = false)
    private String isbn;

    @Column(name = "publication_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime publicationDate;

    @Column(name = "page_count", nullable = false)
    private Integer pageCount;

    public String toString() {
        return this.product + " " + this.author + " " + this.publisher + " " + this.isbn + " " + this.publicationDate + " " + this.pageCount;
    }
}
