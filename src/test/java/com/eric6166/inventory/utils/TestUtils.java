package com.eric6166.inventory.utils;

import com.eric6166.inventory.dto.InventoryDto;
import com.eric6166.inventory.dto.OrderCreatedEventPayload;
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

    public static Product mockProduct(Long id, Long categoryId) {
        return Product.builder()
                .id(id)
                .categoryId(categoryId)
                .description(RandomStringUtils.random(255))
                .name(RandomStringUtils.random(255))
                .price(BigDecimal.valueOf(RandomUtils.nextDouble(1, 100000)))
                .createdBy(RandomStringUtils.random(30))
                .createdDate(LocalDateTime.now())
                .lastModifiedBy(RandomStringUtils.random(30))
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

    public static OrderCreatedEventPayload.Item mockOrderCreatedEventPayloadItem(Long productId, Integer orderQuantity) {
        return OrderCreatedEventPayload.Item.builder()
                .orderQuantity(orderQuantity)
                .productId(productId)
                .build();
    }

    public static InventoryDto mockInventoryDto(Long productId, Long inventoryId, Integer inventoryQuantity, BigDecimal productPrice) {
        return InventoryDto.builder()
                .inventoryId(inventoryId)
                .inventoryQuantity(inventoryQuantity)
                .productId(productId)
                .productPrice(productPrice)
                .build();
    }

}
