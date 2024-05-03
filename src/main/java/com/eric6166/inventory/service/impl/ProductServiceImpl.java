package com.eric6166.inventory.service.impl;

import com.eric6166.base.dto.MessageResponse;
import com.eric6166.base.exception.AppNotFoundException;
import com.eric6166.inventory.dto.CreateProductRequest;
import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.inventory.dto.UpdateProductRequest;
import com.eric6166.inventory.model.Product;
import com.eric6166.inventory.repository.ProductRepository;
import com.eric6166.inventory.service.CacheService;
import com.eric6166.inventory.service.ProductService;
import com.eric6166.inventory.utils.Constants;
import com.eric6166.jpa.dto.PageResponse;
import com.eric6166.jpa.utils.PageUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
    CacheService cacheService;

    @Override
    public PageResponse<ProductDto> findAll(Integer pageNumber, Integer pageSize, String sortColumn, String sortDirection) {
        var pageable = PageUtils.buildPageable(pageNumber, pageSize, sortColumn, sortDirection);
        var page = productRepository.findAll(pageable);
        var content = page.stream().map(product -> modelMapper.map(product, ProductDto.class)).toList();
        return new PageResponse<>(content, page);
    }


    @Cacheable(cacheNames = {Constants.CACHE_NAME_PRODUCT_FIND_BY_ID}, key = "#id")
    @Override
    public ProductDto findById(Long id) throws AppNotFoundException {
        log.debug("ProductServiceImpl.findById");
        return modelMapper.map(getById(id), ProductDto.class);

    }

    private Product getById(Long id) throws AppNotFoundException {
        return productRepository.findById(id).orElseThrow(
                () -> new AppNotFoundException(String.format("product with id %s", id)));
    }

    private void checkExistsById(Long id) throws AppNotFoundException {
        if (!productRepository.existsById(id)) {
            throw new AppNotFoundException(String.format("product with id %s", id));
        }
    }

    @Transactional
    @Override
    public MessageResponse update(UpdateProductRequest request) throws AppNotFoundException {
        log.debug("ProductServiceImpl.update");
        var product = getById(request.getId());
        modelMapper.map(request, product);
        productRepository.saveAndFlush(product);
        cacheService.evictProductFindById(request.getId());
        return new MessageResponse(request.getId(), "Updated Successfully");
    }

    @Transactional
    @Override
    public MessageResponse create(CreateProductRequest request) {
        log.debug("ProductServiceImpl.create");
        var product = modelMapper.map(request, Product.class);
        productRepository.saveAndFlush(product);
        return new MessageResponse(product.getId(), "Created Successfully");
    }

    @Transactional
    @Override
    public void deleteById(Long id) throws AppNotFoundException {
        log.debug("ProductServiceImpl.deleteById");
        checkExistsById(id);
        productRepository.deleteById(id);
        cacheService.evictProductFindById(id);
    }
}
