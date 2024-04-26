package com.eric6166.inventory.controller;

import com.eric6166.common.utils.Const;
import com.eric6166.common.validation.ValidEnumString;
import com.eric6166.common.validation.ValidString;
import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.inventory.service.ProductService;
import com.eric6166.inventory.utils.Constants;
import com.eric6166.jpa.dto.AppResponse;
import com.eric6166.jpa.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Validated
@RequestMapping("/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<AppResponse<PageResponse<ProductDto>>> findAll(
            @RequestParam(required = false, defaultValue = Const.DEFAULT_PAGE_NUMBER_STRING)
            @Min(value = Const.DEFAULT_PAGE_NUMBER)
            @Max(value = Const.DEFAULT_MAX_INTEGER) Integer pageNumber,
            @RequestParam(required = false, defaultValue = Const.DEFAULT_PAGE_SIZE_STRING)
            @Min(value = Const.DEFAULT_PAGE_SIZE)
            @Max(value = Const.MAXIMUM_PAGE_SIZE) Integer pageSize,
            @RequestParam(required = false, defaultValue = Const.DEFAULT_SORT_COLUMN)
            @ValidString(values = {
                    Constants.SORT_COLUMN_ID,
                    Constants.SORT_COLUMN_NAME,
                    Constants.SORT_COLUMN_DESCRIPTION,
                    Constants.SORT_COLUMN_PRICE,
                    Constants.SORT_COLUMN_CATEGORY_ID,
            }) String sortColumn,
            @RequestParam(required = false, defaultValue = Const.DEFAULT_SORT_DIRECTION)
            @ValidEnumString(value = Sort.Direction.class, caseSensitive = false) String sortDirection) {
        var data = productService.findAll(pageNumber, pageSize, sortColumn, sortDirection);
        if (!data.getPageable().isHasContent()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(new AppResponse<>(data));
    }


}
