package com.eric6166.inventory.service.impl;

import com.eric6166.base.dto.MessageResponse;
import com.eric6166.base.exception.AppNotFoundException;
import com.eric6166.common.config.cache.AppCacheManager;
import com.eric6166.inventory.dto.CreateProductRequest;
import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.inventory.dto.TestCacheRequest;
import com.eric6166.inventory.dto.UpdateProductRequest;
import com.eric6166.inventory.model.Product;
import com.eric6166.inventory.repository.ProductRepository;
import com.eric6166.inventory.service.ProductService;
import com.eric6166.inventory.utils.Constants;
import com.eric6166.jpa.dto.PageResponse;
import com.eric6166.jpa.utils.PageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductServiceImpl implements ProductService {

    ProductRepository productRepository;
    ModelMapper modelMapper;
    AppCacheManager appCacheManager;

    @Override
    public PageResponse<ProductDto> findAll(Integer pageNumber, Integer pageSize, String sortColumn, String sortDirection) {
        log.info("ProductServiceImpl.findAll");
        var pageable = PageUtils.buildPageable(pageNumber, pageSize, sortColumn, sortDirection);
        var page = productRepository.findAll(pageable);
        var content = page.stream().map(product -> modelMapper.map(product, ProductDto.class)).toList();
        return new PageResponse<>(content, page);
    }


    @Cacheable(cacheNames = {Constants.CACHE_NAME_PRODUCT_FIND_BY_ID}, key = "#id")
    @Override
    public ProductDto findById(Long id) throws AppNotFoundException {
        log.info("ProductServiceImpl.findById");
        var product = productRepository.findById(id).orElseThrow(
                () -> new AppNotFoundException(String.format("product with id '%s'", id)));
        return modelMapper.map(product, ProductDto.class);

    }

    @Transactional
    @Override
    public MessageResponse update(UpdateProductRequest request) throws AppNotFoundException {
        log.info("ProductServiceImpl.update");
        var product = productRepository.findById(request.getId()).orElseThrow(
                () -> new AppNotFoundException(String.format("product with id '%s'", request.getId())));
        modelMapper.map(request, product);
        productRepository.saveAndFlush(product);
        appCacheManager.evict(Constants.CACHE_NAME_PRODUCT_FIND_BY_ID, request.getId());
        return MessageResponse.builder()
                .message("Updated Successfully")
                .build();
    }

    @Transactional
    @Override
    public MessageResponse create(CreateProductRequest request) {
        log.info("ProductServiceImpl.create");
        var product = modelMapper.map(request, Product.class);
        productRepository.saveAndFlush(product);
        return MessageResponse.builder()
                .id(product.getId())
                .message("Created Successfully")
                .build();
    }

    @Transactional
    @Override
    public void deleteById(Long id) throws AppNotFoundException {
        log.info("ProductServiceImpl.deleteById");
        if (!productRepository.existsById(id)) {
            throw new AppNotFoundException(String.format("product with id '%s'", id));
        }
        productRepository.deleteById(id);
        appCacheManager.evict(Constants.CACHE_NAME_PRODUCT_FIND_BY_ID, id);
    }

//    @Override
//    public void cacheTest(TestCacheRequest testCacheRequest) {
//        if (StringUtils.isBlank(testCacheRequest.getCacheName())) {
//            appCacheManager.clear();
//        } else if (StringUtils.isBlank(testCacheRequest.getCacheKey())) {
//            appCacheManager.clear(testCacheRequest.getCacheName());
//        } else {
//            appCacheManager.evict(testCacheRequest.getCacheName(), testCacheRequest.getCacheKey());
//        }
//    }
//
//    @Override
//    public void getCacheTest(String cacheName, String cacheKey) {
//        if (StringUtils.isBlank(cacheName)) {
//            var cacheNames = appCacheManager.getCacheNames();
//            log.info("cacheNames: {}", cacheNames.toString());
//        } else if (StringUtils.isBlank(cacheKey)) {
//            var cache = appCacheManager.getCache(cacheName);
//        } else {
//            var valueWrapper = appCacheManager.getCache(cacheName, cacheKey);
//            if (valueWrapper != null) {
//                Object o = valueWrapper.get();
//                if (o instanceof ProductDto dto) {
//                    log.info("cache value: {}", dto);
//                }
//            }
//        }
//    }
}
