package com.eric6166.inventory.controller;

import com.eric6166.base.dto.AppResponse;
import com.eric6166.base.utils.BaseConst;
import com.eric6166.base.utils.TestConst;
import com.eric6166.inventory.dto.ProductDto;
import com.eric6166.inventory.service.ProductService;
import com.eric6166.inventory.utils.Constants;
import com.eric6166.inventory.utils.TestUtils;
import com.eric6166.jpa.dto.PageResponse;
import com.eric6166.jpa.utils.PageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(controllers = {ProductController.class})
class ProductControllerTest {

    private static final String URL_TEMPLATE = "/product";
    private static ProductDto productDto;
    private static ProductDto productDto1;
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ProductService productService;

    @BeforeAll
    static void setUpAll() {
        productDto = TestUtils.mockProductDto(TestUtils.mockProduct(RandomUtils.nextLong(1, 100), RandomUtils.nextLong(1, 100)));
        productDto1 = TestUtils.mockProductDto(TestUtils.mockProduct(RandomUtils.nextLong(101, 200), RandomUtils.nextLong(101, 200)));
    }

//    @BeforeEach
//    void setUp() {
//    }
//
//    @AfterEach
//    void tearDown() {
//    }

    @Test
    void findAll_givenPageNumberLessThan1_thenThrowConstraintViolationException() throws Exception {
        var servletException = Assertions.assertThrows(ServletException.class,
                () -> {
                    var pageNumber = -RandomUtils.nextInt(0, BaseConst.DEFAULT_MAX_INTEGER);
                    var pageSize = RandomUtils.nextInt(BaseConst.DEFAULT_PAGE_SIZE, BaseConst.MAXIMUM_PAGE_SIZE);
                    var sortColumn = Constants.SORT_COLUMN_ID;
                    var sortDirection = BaseConst.DEFAULT_SORT_DIRECTION;

                    mvc.perform(MockMvcRequestBuilders
                            .get(URL_TEMPLATE)
                            .with(SecurityMockMvcRequestPostProcessors
                                    .jwt()
                                    .authorities(new SimpleGrantedAuthority(TestConst.ROLE_CUSTOMER)))
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .param("pageNumber", String.valueOf(pageNumber))
                            .param("pageSize", String.valueOf(pageSize))
                            .param("sortColumn", sortColumn)
                            .param("sortDirection", sortDirection));
                });

        var expected = "findAll.pageNumber: must be greater than or equal to 1";

        Assertions.assertTrue(servletException.getCause() instanceof ConstraintViolationException);
        Assertions.assertEquals(expected, servletException.getCause().getMessage());
    }

    @Test
    void findAll_givenDataHasNoContent_thenReturnNoContent() throws Exception {
        var pageNumber = RandomUtils.nextInt(BaseConst.DEFAULT_PAGE_NUMBER, BaseConst.DEFAULT_MAX_INTEGER);
        var pageSize = RandomUtils.nextInt(BaseConst.DEFAULT_PAGE_SIZE, BaseConst.MAXIMUM_PAGE_SIZE);
        var sortColumn = Constants.SORT_COLUMN_ID;
        var sortDirection = BaseConst.DEFAULT_SORT_DIRECTION;
        var pageable = PageUtils.buildPageable(pageNumber, pageSize, sortColumn, sortDirection);
        List<ProductDto> content = new ArrayList<>();
        var page = new PageResponse<>(content, new PageImpl<>(content, pageable, content.size()));
        Mockito.when(productService.findAll(pageNumber, pageSize, sortColumn, sortDirection)).thenReturn(page);
        mvc.perform(MockMvcRequestBuilders
                        .get(URL_TEMPLATE)
                        .with(SecurityMockMvcRequestPostProcessors
                                .jwt()
                                .authorities(new SimpleGrantedAuthority(TestConst.ROLE_CUSTOMER)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sortColumn", sortColumn)
                        .param("sortDirection", sortDirection))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    void findAll_givenSortColumnId_thenReturnOk() throws Exception {
        var pageNumber = RandomUtils.nextInt(BaseConst.DEFAULT_PAGE_NUMBER, BaseConst.DEFAULT_MAX_INTEGER);
        var pageSize = RandomUtils.nextInt(BaseConst.DEFAULT_PAGE_SIZE, BaseConst.MAXIMUM_PAGE_SIZE);
        var sortColumn = Constants.SORT_COLUMN_ID;
        var sortDirection = BaseConst.DEFAULT_SORT_DIRECTION;
        var pageable = PageUtils.buildPageable(pageNumber, pageSize, sortColumn, sortDirection);
        var content = List.of(productDto, productDto1);
        var page = new PageResponse<>(content, new PageImpl<>(content, pageable, content.size()));
        var expected = new AppResponse<>(page);
        Mockito.when(productService.findAll(pageNumber, pageSize, sortColumn, sortDirection)).thenReturn(page);
        mvc.perform(MockMvcRequestBuilders
                        .get(URL_TEMPLATE)
                        .with(SecurityMockMvcRequestPostProcessors
                                .jwt()
                                .authorities(new SimpleGrantedAuthority(TestConst.ROLE_CUSTOMER)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sortColumn", sortColumn)
                        .param("sortDirection", sortDirection))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expected)));
    }

    @Test
    void findAll_givenSortColumnCategoryId_thenReturnOk() throws Exception {
        var pageNumber = RandomUtils.nextInt(BaseConst.DEFAULT_PAGE_NUMBER, BaseConst.DEFAULT_MAX_INTEGER);
        var pageSize = RandomUtils.nextInt(BaseConst.DEFAULT_PAGE_SIZE, BaseConst.MAXIMUM_PAGE_SIZE);
        var sortColumn = Constants.SORT_COLUMN_CATEGORY_ID;
        var sortDirection = BaseConst.DEFAULT_SORT_DIRECTION;
        var pageable = PageUtils.buildPageable(pageNumber, pageSize, sortColumn, sortDirection);
        var content = List.of(productDto1, productDto);
        var page = new PageResponse<>(content, new PageImpl<>(content, pageable, content.size()));
        var expected = new AppResponse<>(page);
        Mockito.when(productService.findAll(pageNumber, pageSize, sortColumn, sortDirection)).thenReturn(page);
        mvc.perform(MockMvcRequestBuilders
                        .get(URL_TEMPLATE)
                        .with(SecurityMockMvcRequestPostProcessors
                                .jwt()
                                .authorities(new SimpleGrantedAuthority(TestConst.ROLE_CUSTOMER)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .param("pageNumber", String.valueOf(pageNumber))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sortColumn", sortColumn)
                        .param("sortDirection", sortDirection))
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
    void findById_givenIdLessThan1_thenThrowConstraintViolationException() {
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