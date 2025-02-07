package com.shopme.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class BookResponseDto {
    private Long id;
    private String author;
    private String publisher;
    private String isbn;
    private Date publicationDate;
    private Integer pageCount;
}
