package com.shopme.mapper;

import com.shopme.common.entity.Seller;
import com.shopme.common.shop.Book;
import com.shopme.common.shop.Category;
import com.shopme.dto.request.BookCreateDto;
import com.shopme.dto.response.BookResponseDto;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {
    public Book toEntity(BookCreateDto bookCreateDto, Category category, Seller seller) {
        Book book = Book.builder()
                .author(bookCreateDto.getAuthor())
                .isbn(bookCreateDto.getIsbn())
                .publisher(bookCreateDto.getPublisher())
                .publicationDate(bookCreateDto.getPublicationDate())
                .pageCount(bookCreateDto.getPageCount())
                .name(bookCreateDto.getName())
                .alias(bookCreateDto.getAlias())
                .name(bookCreateDto.getName())
                .alias(bookCreateDto.getAlias())
                .shortDescription(bookCreateDto.getShortDescription())
                .fullDescription(bookCreateDto.getFullDescription())
                .createdTime(bookCreateDto.getCreatedTime())
                .updatedTime(bookCreateDto.getUpdatedTime())
                .enabled(bookCreateDto.isEnabled())
                .stockQuantity(bookCreateDto.getStockQuantity())
                .price(bookCreateDto.getPrice())
                .currency(bookCreateDto.getCurrency())
                .rating(bookCreateDto.getRating())
                .weight(bookCreateDto.getWeight())
                .mainImage(bookCreateDto.getMainImage())
                .category(category)
                .seller(seller)
                .build();
        return book;
    }

    public BookResponseDto toDto(Book book) {
        BookResponseDto bookResponseDto = BookResponseDto.builder()
                .id(book.getId())
                .author(book.getAuthor())
                .publisher(book.getPublisher())
                .isbn(book.getIsbn())
                .publicationDate(book.getPublicationDate())
                .pageCount(book.getPageCount())
                .build();
        return bookResponseDto;
    }
}
