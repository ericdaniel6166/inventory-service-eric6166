package com.eric6166.inventory.service.impl;

import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.inventory.repository.ProductRepository;
import com.eric6166.inventory.service.ProductService;
import com.eric6166.jpa.dto.PageResponse;
import com.eric6166.jpa.utils.PageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    ModelMapper modelMapper;

    @Override
    public PageResponse<ProductDto> findAll(Integer pageNumber, Integer pageSize, String sortColumn, String sortDirection) {
        var pageable = PageUtils.buildPageable(pageNumber, pageSize, sortColumn, sortDirection);
        var page = productRepository.findAll(pageable);
        var content = page.stream().map(product -> modelMapper.map(product, ProductDto.class)).toList();
        return new PageResponse<>(content, page);
    }
}
