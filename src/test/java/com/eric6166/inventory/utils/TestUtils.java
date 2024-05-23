package com.eric6166.inventory.utils;

import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.inventory.model.Product;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TestUtils {

    private TestUtils() {
        throw new IllegalStateException("Utility class");
    }

//    public static ProductDto mockProductDto() {
//        return ProductDto.builder()
//                .id(RandomUtils.nextLong(1, 100))
//                .categoryId(RandomUtils.nextLong(1, 100))
//                .description(RandomStringUtils.randomAlphabetic(10))
//                .name(RandomStringUtils.randomAlphabetic(10))
//                .price(BigDecimal.valueOf(RandomUtils.nextDouble(1, 10000)))
//                .createdBy(RandomStringUtils.randomAlphabetic(10))
//                .createdDate(LocalDateTime.now())
//                .lastModifiedBy(RandomStringUtils.randomAlphabetic(10))
//                .lastModifiedDate(LocalDateTime.now())
//                .build();
//    }

    public static ProductDto mockProductDto(Product product) {
        return ProductDto.builder()
                .id(product.getId())
                .categoryId(product.getCategoryId())
                .description(product.getDescription())
                .name(product.getName())
                .price(product.getPrice())
                .createdBy(product.getCreatedBy())
                .createdDate(product.getCreatedDate())
                .lastModifiedBy(product.getLastModifiedBy())
                .lastModifiedDate(product.getLastModifiedDate())
                .build();
    }

    public static Product mockProduct() {
        return Product.builder()
                .id(RandomUtils.nextLong(1, 100))
                .categoryId(RandomUtils.nextLong(1, 100))
                .description(RandomStringUtils.randomAlphabetic(10))
                .name(RandomStringUtils.randomAlphabetic(10))
                .price(BigDecimal.valueOf(RandomUtils.nextDouble(1, 10000)))
                .createdBy(RandomStringUtils.randomAlphabetic(10))
                .createdDate(LocalDateTime.now())
                .lastModifiedBy(RandomStringUtils.randomAlphabetic(10))
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }
}
