package com.shopme.mapper;

import com.shopme.common.entity.Seller;
import com.shopme.common.shop.Book;
import com.shopme.common.shop.Category;
import com.shopme.dto.request.BookCreateDto;
import com.shopme.dto.request.BookUpdateDto;
import com.shopme.dto.response.BookResponseDto;
import jakarta.validation.Valid;
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
        BookResponseDto bookResponseDto = new BookResponseDto(book);
        return bookResponseDto;
    }

    public void updateFromDto(@Valid BookUpdateDto bookUpdateDto, Book book, Category category) {
        book.setName(bookUpdateDto.getName() != null ? bookUpdateDto.getName() : book.getName());
        book.setShortDescription(bookUpdateDto.getShortDescription() != null ? bookUpdateDto.getShortDescription() : book.getShortDescription());
        book.setFullDescription(bookUpdateDto.getFullDescription() != null ? bookUpdateDto.getFullDescription() : book.getFullDescription());
        book.setUpdatedTime(bookUpdateDto.getUpdatedTime());
        book.setEnabled(bookUpdateDto.getEnabled() != null ? bookUpdateDto.getEnabled() : book.isEnabled());
        book.setStockQuantity(bookUpdateDto.getStockQuantity() != null ? bookUpdateDto.getStockQuantity() : book.getStockQuantity());
        book.setPrice(bookUpdateDto.getPrice() != null ? bookUpdateDto.getPrice() : book.getPrice());
        book.setCurrency(bookUpdateDto.getCurrency() != null ? bookUpdateDto.getCurrency() : book.getCurrency());
        book.setRating(bookUpdateDto.getRating() != null ? bookUpdateDto.getRating() : book.getRating());
        book.setWeight(bookUpdateDto.getWeight() != null ? bookUpdateDto.getWeight() : book.getWeight());
        book.setMainImage(bookUpdateDto.getMainImage() != null ? bookUpdateDto.getMainImage() : book.getMainImage());
        book.setAuthor(bookUpdateDto.getAuthor() != null ? bookUpdateDto.getAuthor() : book.getAuthor());
        book.setPublisher(bookUpdateDto.getPublisher() != null ? bookUpdateDto.getPublisher() : book.getPublisher());
        book.setPublicationDate(bookUpdateDto.getPublicationDate() != null ? bookUpdateDto.getPublicationDate() : book.getPublicationDate());
        book.setPageCount(bookUpdateDto.getPageCount() != null ? bookUpdateDto.getPageCount() : book.getPageCount());
        book.setPageCount(bookUpdateDto.getPageCount() != null ? bookUpdateDto.getPageCount() : book.getPageCount());
        book.setCategory(bookUpdateDto.getCategoryId() != null ? category : book.getCategory());
    }
}
