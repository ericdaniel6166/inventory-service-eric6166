package com.eric6166.inventory.service.impl;

import com.eric6166.base.dto.MessageResponse;
import com.eric6166.base.exception.AppNotFoundException;
import com.eric6166.base.utils.BaseConst;
import com.eric6166.common.config.cache.AppCacheManager;
import com.eric6166.inventory.dto.CreateProductRequest;
import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.inventory.dto.UpdateProductRequest;
import com.eric6166.inventory.model.Product;
import com.eric6166.inventory.repository.ProductRepository;
import com.eric6166.inventory.utils.TestUtils;
import com.eric6166.jpa.dto.PageResponse;
import com.eric6166.jpa.utils.PageUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ProductServiceImplTest {

    private static ProductDto productDto;
    private static ProductDto productDto1;
    private static Product product;
    private static Product product1;
    @InjectMocks
    ProductServiceImpl productService;
    @Mock
    ModelMapper modelMapper;
    @Mock
    ProductRepository productRepository;
    @Mock
    AppCacheManager appCacheManager;


    @BeforeAll
    static void setUpAll() {
        product = TestUtils.mockProduct(1L, 2L);
        productDto = TestUtils.mockProductDto(product);
        product1 = TestUtils.mockProduct(2L, 1L);
        productDto1 = TestUtils.mockProductDto(product1);

    }

//    @BeforeEach
//    void setUp() {
//    }
//
//    @AfterEach
//    void tearDown() {
//    }//    @BeforeEach
////    void setUp() {
////    }
////
////    @AfterEach
////    void tearDown() {
////    }

    @Test
    void deleteById_thenThrowAppNotFoundException() {
        var id = RandomUtils.nextLong(1, 100);
        var e = Assertions.assertThrows(AppNotFoundException.class,
                () -> {
                    Mockito.when(productRepository.existsById(id)).thenReturn(false);

                    productService.deleteById(id);
                });
        var expected = String.format("product with id '%s' not found", id);

        Assertions.assertEquals(expected, e.getMessage());

    }

    @Test
    void deleteById_thenReturnSuccess() throws AppNotFoundException {
        var id = product.getId();
        Mockito.when(productRepository.existsById(id)).thenReturn(true);

        productService.deleteById(id);

        Mockito.verify(productRepository, Mockito.times(1)).existsById(id);
        Mockito.verify(productRepository, Mockito.times(1)).deleteById(id);

    }

    @Test
    void create_thenReturnSuccess() {
        var request = CreateProductRequest.builder()
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
        Mockito.when(modelMapper.map(request, Product.class)).thenReturn(product);
        var expected = MessageResponse.builder()
                .id(product.getId())
                .message("Created Successfully")
                .build();

        var actual = productService.create(request);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void update_thenThrowAppNotFoundException() {
        var id = RandomUtils.nextLong(1, 100);
        var request = UpdateProductRequest.builder()
                .id(id)
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
        var e = Assertions.assertThrows(AppNotFoundException.class,
                () -> {
                    Mockito.when(productRepository.findById(id)).thenReturn(Optional.empty());

                    productService.update(request);
                });
        var expected = String.format("product with id '%s' not found", id);

        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void update_thenReturnSuccess() throws AppNotFoundException {
        var id = product.getId();
        var request = UpdateProductRequest.builder()
                .id(id)
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .build();
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        var expected = MessageResponse.builder()
                .message("Updated Successfully")
                .build();

        var actual = productService.update(request);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void findAll_givenSortColumnCategoryId_thenReturnSuccess() {
        var pageNumber = RandomUtils.nextInt(BaseConst.DEFAULT_PAGE_NUMBER, BaseConst.DEFAULT_MAX_INTEGER);
        var pageSize = RandomUtils.nextInt(BaseConst.DEFAULT_PAGE_SIZE, BaseConst.MAXIMUM_PAGE_SIZE);
        var sortColumn = "categoryId";
        var sortDirection = "asc";
        var pageable = PageUtils.buildPageable(pageNumber, pageSize, sortColumn, sortDirection);
        var content1 = List.of(productDto1, productDto);
        var pageList1 = List.of(product1, product);
        var page = new PageImpl<>(pageList1, pageable, content1.size());
        Mockito.when(productRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);
        Mockito.when(modelMapper.map(product1, ProductDto.class)).thenReturn(productDto1);
        var expected = new PageResponse<>(content1, page);

        var actual = productService.findAll(pageNumber, pageSize, sortColumn, sortDirection);

        Assertions.assertEquals(expected.getContent(), actual.getContent());
    }

    @Test
    void findAll_givenSortColumnId_thenReturnSuccess() {
        var pageNumber = RandomUtils.nextInt(BaseConst.DEFAULT_PAGE_NUMBER, BaseConst.DEFAULT_MAX_INTEGER);
        var pageSize = RandomUtils.nextInt(BaseConst.DEFAULT_PAGE_SIZE, BaseConst.MAXIMUM_PAGE_SIZE);
        var sortColumn = "id";
        var sortDirection = "asc";
        var pageable = PageUtils.buildPageable(pageNumber, pageSize, sortColumn, sortDirection);
        var content = List.of(productDto, productDto1);
        var pageList = List.of(product, product1);
        var page = new PageImpl<>(pageList, pageable, content.size());
        Mockito.when(productRepository.findAll(pageable)).thenReturn(page);
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);
        Mockito.when(modelMapper.map(product1, ProductDto.class)).thenReturn(productDto1);
        var expected = new PageResponse<>(content, page);

        var actual = productService.findAll(pageNumber, pageSize, sortColumn, sortDirection);

        Assertions.assertEquals(expected.getContent(), actual.getContent());
    }

    @Test
    void findById_thenThrowAppNotFoundException() {
        var id = RandomUtils.nextLong(1, 100);
        var e = Assertions.assertThrows(AppNotFoundException.class,
                () -> {
                    Mockito.when(productRepository.findById(id)).thenReturn(Optional.empty());

                    productService.findById(id);
                });
        var expected = String.format("product with id '%s' not found", id);

        Assertions.assertEquals(expected, e.getMessage());
    }

    @Test
    void findById_thenReturnSuccess() throws AppNotFoundException {
        var id = product.getId();
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);
        var expected = productDto;

        var actual = productService.findById(id);

        Assertions.assertEquals(expected, actual);
    }


}