package com.shopme.mapper;

import com.shopme.common.shop.Book;
import com.shopme.common.shop.Product;
import com.shopme.dto.response.BookResponseDto;
import com.shopme.dto.response.ProductResponseDto;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ProductMapper {
    private static final Map<Class<? extends Product>, Function<Product, ProductResponseDto>> MAPPER = new HashMap<>();

    static {
        MAPPER.put(Book.class, product -> new BookResponseDto((Book) product));
    }

    public static ProductResponseDto toProductResponseDto(Product product) {
        return MAPPER.getOrDefault(product.getClass(), p -> {
            throw new RuntimeException("This product type is not supported");
        }).apply(product);
    }

    public static void register(Class<? extends Product> productClass, Function<Product, ProductResponseDto> mapperFunction) {
        MAPPER.put(productClass, mapperFunction);
    }
}