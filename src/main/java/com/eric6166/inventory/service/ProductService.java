package com.eric6166.inventory.service;

import com.eric6166.base.dto.MessageResponse;
import com.eric6166.base.exception.AppNotFoundException;
import com.eric6166.inventory.dto.CreateProductRequest;
import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.inventory.dto.TestCacheRequest;
import com.eric6166.inventory.dto.UpdateProductRequest;
import com.eric6166.jpa.dto.PageResponse;

public interface ProductService {
    PageResponse<ProductDto> findAll(Integer pageNumber, Integer pageSize, String sortColumn, String sortDirection);

    ProductDto findById(Long id) throws AppNotFoundException;

    MessageResponse update(UpdateProductRequest request) throws AppNotFoundException;

    MessageResponse create(CreateProductRequest request);

    void deleteById(Long id) throws AppNotFoundException;

    void cacheTest(TestCacheRequest testCacheRequest);

    void getCacheTest(String cacheName, String cacheKey);
}
