package com.shopme.common.shop;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "books")
@DiscriminatorValue("Book")
public class Book extends Product {
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
        return this.author + " " + this.publisher + " " + this.isbn + " " + this.publicationDate + " " + this.pageCount;
    }
}
