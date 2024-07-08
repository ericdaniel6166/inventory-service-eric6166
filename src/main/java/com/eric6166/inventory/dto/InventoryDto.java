package com.eric6166.inventory.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDto {
    private Long productId;
    private BigDecimal productPrice;
    private Long inventoryId;
    private Integer inventoryQuantity;

}
