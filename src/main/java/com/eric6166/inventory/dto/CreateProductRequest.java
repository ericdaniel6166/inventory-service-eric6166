package com.eric6166.inventory.dto;

import com.eric6166.base.utils.BaseConst;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    @Size(max = BaseConst.DEFAULT_SIZE_MAX_STRING)
    private String name;

    @Size(max = BaseConst.SIZE_MAX_STRING)
    private String description;

    @Digits(integer = BaseConst.MAXIMUM_BIG_DECIMAL_INTEGER, fraction = BaseConst.MAXIMUM_BIG_DECIMAL_FRACTION)
    @Positive
    private BigDecimal price;
}
