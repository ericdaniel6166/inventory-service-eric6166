package com.eric6166.inventory.controller;

import com.eric6166.base.dto.AppResponse;
import com.eric6166.base.utils.TestConst;
import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.inventory.service.ProductService;
import com.eric6166.inventory.utils.TestUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;

@WebMvcTest(controllers = {ProductController.class})
@FieldDefaults(level = AccessLevel.PRIVATE)
class ProductControllerTest {

    private static final String URL_TEMPLATE = "/product";
    private static ProductDto productDto;
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ProductService productService;

    @BeforeAll
    static void setUpAll() {
        productDto = TestUtils.mockProductDto(TestUtils.mockProduct());
    }

//    @BeforeEach
//    void setUp() {
//    }
//
//    @AfterEach
//    void tearDown() {
//    }


    @Test
    void findById() throws Exception {
        var id = productDto.getId();
        var expected = new AppResponse<>(productDto);
        Mockito.when(productService.findById(id)).thenReturn(productDto);

        mvc.perform(MockMvcRequestBuilders
                        .get(URL_TEMPLATE + "/" + id)
                        .with(SecurityMockMvcRequestPostProcessors
                                .jwt()
                                .authorities(new SimpleGrantedAuthority(TestConst.ROLE_CUSTOMER)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void findById_thenReturnOk() throws Exception {
        var id = productDto.getId();
        var expected = new AppResponse<>(productDto);
        Mockito.when(productService.findById(id)).thenReturn(productDto);

        mvc.perform(MockMvcRequestBuilders
                        .get(URL_TEMPLATE + "/" + id)
                        .with(SecurityMockMvcRequestPostProcessors
                                .jwt()
                                .authorities(new SimpleGrantedAuthority(TestConst.ROLE_CUSTOMER)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void findById_thenReturnUnauthorized() throws Exception {
        var id = productDto.getId();

        mvc.perform(MockMvcRequestBuilders
                        .get(URL_TEMPLATE + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    void findById_thenThrowConstraintViolationException() {
        var servletException = Assertions.assertThrows(ServletException.class,
                () -> {
                    var id = -RandomUtils.nextLong(0, 100);

                    mvc.perform(MockMvcRequestBuilders
                            .get(URL_TEMPLATE + "/" + id)
                            .with(SecurityMockMvcRequestPostProcessors
                                    .jwt()
                                    .authorities(new SimpleGrantedAuthority(TestConst.ROLE_CUSTOMER)))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .characterEncoding(StandardCharsets.UTF_8));
                });

        var expected = "findById.id: must be greater than or equal to 1";

        Assertions.assertTrue(servletException.getCause() instanceof ConstraintViolationException);
        Assertions.assertEquals(expected, servletException.getCause().getMessage());
    }

}