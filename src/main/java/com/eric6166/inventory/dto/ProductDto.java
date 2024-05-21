package com.eric6166.inventory.dto;

import com.eric6166.jpa.model.BaseEntity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = false)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString(callSuper = true)
public class ProductDto extends BaseEntity<String> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    Long id;
    String name;
    String description;
    BigDecimal price;
    Long categoryId;
}
