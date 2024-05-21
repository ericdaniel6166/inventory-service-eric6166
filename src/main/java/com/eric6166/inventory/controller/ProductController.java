package com.eric6166.inventory.controller;

import com.eric6166.base.dto.AppResponse;
import com.eric6166.base.dto.MessageResponse;
import com.eric6166.base.exception.AppNotFoundException;
import com.eric6166.base.utils.BaseConst;
import com.eric6166.base.validation.ValidEnumString;
import com.eric6166.base.validation.ValidString;
import com.eric6166.inventory.dto.CreateProductRequest;
import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.inventory.dto.TestCacheRequest;
import com.eric6166.inventory.dto.UpdateProductRequest;
import com.eric6166.inventory.service.ProductService;
import com.eric6166.inventory.utils.Constants;
import com.eric6166.jpa.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("product test");
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/cache/test")
    public ResponseEntity<String> cacheTest(@RequestBody TestCacheRequest testCacheRequest) {
        productService.cacheTest(testCacheRequest);
        return ResponseEntity.ok("cache test");
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/cache/test")
    public ResponseEntity<String> getCacheTest(@RequestParam(required = false) String cacheName, @RequestParam(required = false) String cacheKey) {
        productService.getCacheTest(cacheName, cacheKey);
        return ResponseEntity.ok("cache test");
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping
    public ResponseEntity<AppResponse<PageResponse<ProductDto>>> findAll(
            @RequestParam(required = false, defaultValue = BaseConst.DEFAULT_PAGE_NUMBER_STRING)
            @Min(value = BaseConst.DEFAULT_PAGE_NUMBER)
            @Max(value = BaseConst.DEFAULT_MAX_INTEGER) Integer pageNumber,
            @RequestParam(required = false, defaultValue = BaseConst.DEFAULT_PAGE_SIZE_STRING)
            @Min(value = BaseConst.DEFAULT_PAGE_SIZE)
            @Max(value = BaseConst.MAXIMUM_PAGE_SIZE) Integer pageSize,
            @RequestParam(required = false, defaultValue = BaseConst.DEFAULT_SORT_COLUMN)
            @ValidString(values = {
                    Constants.SORT_COLUMN_ID,
                    Constants.SORT_COLUMN_NAME,
                    Constants.SORT_COLUMN_DESCRIPTION,
                    Constants.SORT_COLUMN_PRICE,
                    Constants.SORT_COLUMN_CATEGORY_ID,
            }) String sortColumn,
            @RequestParam(required = false, defaultValue = BaseConst.DEFAULT_SORT_DIRECTION)
            @ValidEnumString(value = Sort.Direction.class, caseSensitive = false) String sortDirection) {
        var data = productService.findAll(pageNumber, pageSize, sortColumn, sortDirection);
        if (!data.getPageable().isHasContent()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(new AppResponse<>(data));
    }


    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/{id}")
    public ResponseEntity<AppResponse<ProductDto>> findById(@PathVariable @NotNull @Min(value = 1)
                                                            @Max(value = BaseConst.DEFAULT_MAX_LONG) Long id)
            throws AppNotFoundException {
        return ResponseEntity.ok(new AppResponse<>(productService.findById(id)));
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponse<MessageResponse>> deleteById(@PathVariable @NotNull @Min(value = 1)
                                                                   @Max(value = BaseConst.DEFAULT_MAX_LONG) Long id)
            throws AppNotFoundException {
        productService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PutMapping
    public ResponseEntity<AppResponse<MessageResponse>> update(@RequestBody @Valid UpdateProductRequest request)
            throws AppNotFoundException {
        return ResponseEntity.ok(new AppResponse<>(productService.update(request)));
    }

    @Operation(security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    public ResponseEntity<AppResponse<MessageResponse>> create(@RequestBody @Valid CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new AppResponse<>(productService.create(request)));
    }

}
