package com.eric6166.inventory.controller;

import com.eric6166.common.utils.CommonUtils;
import com.eric6166.common.utils.Const;
import com.eric6166.common.validation.ValidCollectionString;
import com.eric6166.common.validation.ValidEnumString;
import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.inventory.service.ProductService;
import com.eric6166.inventory.utils.Constants;
import com.eric6166.jpa.dto.AppPageRequest;
import com.eric6166.jpa.dto.PageResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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

import java.util.Set;

@RestController
@Slf4j
@Validated
@RequestMapping("/product")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductController {

    ProductService productService;

    @GetMapping("/test")
    public ResponseEntity<Object> test() {
        return ResponseEntity.ok(CommonUtils.get());
    }

    //    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<PageResponse<ProductDto>> findAll(
            @RequestParam(required = false, defaultValue = Const.STRING_ONE)
            @Min(value = Const.INTEGER_ONE)
            @Max(value = Const.DEFAULT_MAX_INTEGER) Integer pageNumber,
            @RequestParam(required = false, defaultValue = Const.DEFAULT_PAGE_SIZE_STRING)
            @Min(value = Const.DEFAULT_PAGE_SIZE)
            @Max(value = Const.MAXIMUM_PAGE_SIZE) Integer pageSize,
            @RequestParam(required = false, defaultValue = Const.DEFAULT_SORT_COLUMN)
            @ValidCollectionString(values = {
                    Constants.SORT_COLUMN_ID,
                    Constants.SORT_COLUMN_NAME,
                    Constants.SORT_COLUMN_DESCRIPTION,
                    Constants.SORT_COLUMN_PRICE,
                    Constants.SORT_COLUMN_CATEGORY_ID,
            })
            @Size(min = Const.INTEGER_ONE, max = Const.MAXIMUM_SORT_COLUMN) Set<String> sortColumn,
            @RequestParam(required = false, defaultValue = Const.DEFAULT_SORT_DIRECTION)
            @ValidEnumString(value = Sort.Direction.class, caseSensitive = false) String sortDirection) {
        var request = new AppPageRequest(pageNumber, pageSize, sortColumn, sortDirection);
        var response = productService.findAll(request);
        if (!response.getPageable().isHasContent()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response);
    }


}
