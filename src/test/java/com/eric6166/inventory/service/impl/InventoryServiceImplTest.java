package com.eric6166.inventory.service.impl;

import com.eric6166.common.config.kafka.AppEvent;
import com.eric6166.inventory.config.kafka.KafkaProducerProps;
import com.eric6166.inventory.dto.PlaceOrderEventPayload;
import com.eric6166.inventory.repository.InventoryRepository;
import com.eric6166.inventory.utils.TestUtils;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class InventoryServiceImplTest {

    private static PlaceOrderEventPayload.Item item;
    private static PlaceOrderEventPayload.Item item1;
//    private static InventoryDto inventoryDto;
//    private static InventoryDto inventoryDto1;

    @InjectMocks
    InventoryServiceImpl inventoryService;
    @Mock
    InventoryRepository inventoryRepository;
    @Mock
    ModelMapper modelMapper;
    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    KafkaProducerProps kafkaProducerProps;

    @BeforeAll
    static void setUpAll() {
        item = TestUtils.mockPlaceOrderEventPayloadItem(RandomUtils.nextLong(1, 100), RandomUtils.nextInt(1, 10000));
        item1 = TestUtils.mockPlaceOrderEventPayloadItem(RandomUtils.nextLong(1, 100), RandomUtils.nextInt(1, 10000));


    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void handlePlaceOrderEvent_givenOrderQuantityGreaterThanInventoryQuantity_thenSendItemNotAvailableEvent() {
        var orderUuid = UUID.randomUUID().toString();
        var payload = PlaceOrderEventPayload.builder()
                .orderUuid(orderUuid)
                .username("customer")
                .itemList(List.of(item, item1))
                .build();
        var appEvent = AppEvent.builder()
                .uuid(UUID.randomUUID().toString())
                .payload(payload)
                .build();

        var productIds = payload.getItemList().stream()
                .map(PlaceOrderEventPayload.Item::getProductId)
                .toList();
        var inventoryDto = TestUtils.mockInventoryDto(item.getProductId(), RandomUtils.nextLong(1, 100),
                RandomUtils.nextInt(0, item.getOrderQuantity() - 1),
                BigDecimal.valueOf(RandomUtils.nextDouble(1, 10000)));
        var inventoryDto1 = TestUtils.mockInventoryDto(item1.getProductId(), RandomUtils.nextLong(1, 100),
                RandomUtils.nextInt(item1.getOrderQuantity(), 10000),
                BigDecimal.valueOf(RandomUtils.nextDouble(1, 100000)));
        var inventoryDtoList = List.of(inventoryDto, inventoryDto1);

        var topicName = "item-not-available";

        Mockito.when(modelMapper.map(appEvent.getPayload(), PlaceOrderEventPayload.class)).thenReturn(payload);
        Mockito.when(kafkaProducerProps.getItemNotAvailableTopicName()).thenReturn(topicName);
        Mockito.when(inventoryRepository.findAllInventoryByProductIdIn(productIds)).thenReturn(inventoryDtoList);

        inventoryService.handlePlaceOrderEvent(appEvent);

        Mockito.verify(inventoryRepository, Mockito.times(1)).findAllInventoryByProductIdIn(productIds);
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(Mockito.eq(topicName), Mockito.any());
    }

    @Test
    void handlePlaceOrderEvent_givenInventoryDtoNotFound_thenSendItemNotAvailableEvent() {
        var orderUuid = UUID.randomUUID().toString();
        var payload = PlaceOrderEventPayload.builder()
                .orderUuid(orderUuid)
                .username("customer")
                .itemList(List.of(item, item1))
                .build();
        var appEvent = AppEvent.builder()
                .uuid(UUID.randomUUID().toString())
                .payload(payload)
                .build();

        var productIds = payload.getItemList().stream()
                .map(PlaceOrderEventPayload.Item::getProductId)
                .toList();
        var inventoryDto1 = TestUtils.mockInventoryDto(item1.getProductId(), RandomUtils.nextLong(1, 100),
                RandomUtils.nextInt(item1.getOrderQuantity(), 10000),
                BigDecimal.valueOf(RandomUtils.nextDouble(1, 100000)));

        var topicName = "item-not-available";

        Mockito.when(modelMapper.map(appEvent.getPayload(), PlaceOrderEventPayload.class)).thenReturn(payload);
        Mockito.when(kafkaProducerProps.getItemNotAvailableTopicName()).thenReturn(topicName);
        Mockito.when(inventoryRepository.findAllInventoryByProductIdIn(productIds)).thenReturn(Collections.singletonList(inventoryDto1));

        inventoryService.handlePlaceOrderEvent(appEvent);

        Mockito.verify(inventoryRepository, Mockito.times(1)).findAllInventoryByProductIdIn(productIds);
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(Mockito.eq(topicName), Mockito.any());
    }

    @Test
    void handlePlaceOrderEvent_thenSendInventoryCheckedEvent() {
        var orderUuid = UUID.randomUUID().toString();
        var payload = PlaceOrderEventPayload.builder()
                .orderUuid(orderUuid)
                .username("customer")
                .itemList(List.of(item, item1))
                .build();
        var appEvent = AppEvent.builder()
                .uuid(UUID.randomUUID().toString())
                .payload(payload)
                .build();

        var productIds = payload.getItemList().stream()
                .map(PlaceOrderEventPayload.Item::getProductId)
                .toList();
        var inventoryDto = TestUtils.mockInventoryDto(item.getProductId(), RandomUtils.nextLong(1, 100),
                RandomUtils.nextInt(item.getOrderQuantity(), 10000),
                BigDecimal.valueOf(RandomUtils.nextDouble(1, 10000)));
        var inventoryDto1 = TestUtils.mockInventoryDto(item1.getProductId(), RandomUtils.nextLong(1, 100),
                RandomUtils.nextInt(item1.getOrderQuantity(), 10000),
                BigDecimal.valueOf(RandomUtils.nextDouble(1, 100000)));
        var inventoryDtoList = List.of(inventoryDto, inventoryDto1);

        var topicName = "inventory-checked";

        Mockito.when(modelMapper.map(appEvent.getPayload(), PlaceOrderEventPayload.class)).thenReturn(payload);
        Mockito.when(kafkaProducerProps.getInventoryCheckedTopicName()).thenReturn(topicName);
        Mockito.when(inventoryRepository.findAllInventoryByProductIdIn(productIds)).thenReturn(inventoryDtoList);

        inventoryService.handlePlaceOrderEvent(appEvent);

        Mockito.verify(inventoryRepository, Mockito.times(1)).findAllInventoryByProductIdIn(productIds);
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(Mockito.eq(topicName), Mockito.any());
    }


}