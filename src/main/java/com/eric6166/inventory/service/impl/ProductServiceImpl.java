package com.eric6166.inventory.service.impl;

import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.inventory.repository.ProductRepository;
import com.eric6166.inventory.service.ProductService;
import com.eric6166.jpa.dto.AppPageRequest;
import com.eric6166.jpa.dto.PageResponse;
import com.eric6166.jpa.utils.PageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    ModelMapper modelMapper;

    @Override
    public PageResponse<ProductDto> findAll(AppPageRequest request) {
        List<Sort.Order> orders = List.of(new Sort.Order(
                Sort.Direction.fromString(request.getSortDirection()),
                request.getSortColumn()));
        PageUtils.addDefaultOrder(orders);
        return findAll(request.getPageNumber(),
                request.getPageSize(),
                Sort.by(orders));
    }

    private PageResponse<ProductDto> findAll(Integer pageNumber, Integer pageSize, Sort sort) {
        var pageable = PageUtils.buildPageable(pageNumber, pageSize, sort);
        var page = productRepository.findAll(pageable);
        var content = page.stream().map(product -> modelMapper.map(product, ProductDto.class)).toList();
        return new PageResponse<>(content, page);
    }
}
