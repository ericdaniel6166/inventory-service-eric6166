package com.eric6166.inventory.service;

import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.jpa.dto.AppPageRequest;
import com.eric6166.jpa.dto.PageResponse;

public interface ProductService {

    PageResponse<ProductDto> findAll(AppPageRequest request);
}
