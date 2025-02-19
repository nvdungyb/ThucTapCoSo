package com.shopme.mapper;

import com.shopme.common.shop.Book;
import com.shopme.common.shop.Laptop;
import com.shopme.common.shop.Product;
import com.shopme.dto.response.BookEntityResponse;
import com.shopme.dto.response.LaptopEntityResponse;
import com.shopme.dto.response.ProductResponseDto;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class ProductMapper {
    private static final Map<Class<? extends Product>, Function<Product, ProductResponseDto>> MAPPER = new HashMap<>();

    static {
        MAPPER.put(Book.class, product -> new BookEntityResponse((Book) product));
        MAPPER.put(Laptop.class, product -> new LaptopEntityResponse((Laptop) product));
    }

    public ProductResponseDto toProductResponseDto(Product product) {
        return MAPPER.getOrDefault(product.getClass(), p -> {
            throw new RuntimeException("This product type is not supported. Please add a mapper for this type.");
        }).apply(product);
    }

    public static void register(Class<? extends Product> productClass, Function<Product, ProductResponseDto> mapperFunction) {
        MAPPER.put(productClass, mapperFunction);
    }
}