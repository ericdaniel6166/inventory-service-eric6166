package com.eric6166.inventory.service.impl;

import com.eric6166.base.exception.AppNotFoundException;
import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.inventory.model.Product;
import com.eric6166.inventory.repository.ProductRepository;
import com.eric6166.inventory.utils.TestUtils;
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

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ProductServiceImplTest {

    private static ProductDto productDto;
    private static Product product;
    @InjectMocks
    ProductServiceImpl productService;
    @Mock
    ModelMapper modelMapper;
    @Mock
    ProductRepository productRepository;

    @BeforeAll
    static void setUpAll() {
        product = TestUtils.mockProduct();
        productDto = TestUtils.mockProductDto(product);
    }

//    @BeforeEach
//    void setUp() {
//    }
//
//    @AfterEach
//    void tearDown() {
//    }

    @Test
    void findById() throws AppNotFoundException {
        var id = product.getId();
        Mockito.when(productRepository.findById(id)).thenReturn(Optional.of(product));
        Mockito.when(modelMapper.map(product, ProductDto.class)).thenReturn(productDto);
        var expected = productDto;

        var actual = productService.findById(id);

        Assertions.assertEquals(expected, actual);
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