package com.eric6166.inventory.dto;

import com.eric6166.base.utils.BaseConst;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateProductRequest {
    @Size(max = BaseConst.DEFAULT_SIZE_MAX_STRING)
    String name;

    @Size(max = BaseConst.SIZE_MAX_STRING)
    String description;

    @Digits(integer = BaseConst.MAXIMUM_BIG_DECIMAL_INTEGER, fraction = BaseConst.MAXIMUM_BIG_DECIMAL_FRACTION)
    @Positive
    BigDecimal price;
}
