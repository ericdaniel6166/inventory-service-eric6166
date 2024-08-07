package com.eric6166.inventory.service.impl;

import com.eric6166.common.config.kafka.AppEvent;
import com.eric6166.inventory.config.kafka.KafkaProducerProps;
import com.eric6166.inventory.dto.OrderCreatedEventPayload;
import com.eric6166.inventory.repository.InventoryRepository;
import com.eric6166.inventory.utils.TestUtils;
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
class InventoryServiceImplTest {

    private static OrderCreatedEventPayload.Item item;
    private static OrderCreatedEventPayload.Item item1;

    @InjectMocks
    private InventoryServiceImpl inventoryService;
    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private KafkaProducerProps kafkaProducerProps;

    @BeforeAll
    static void setUpAll() {
        item = TestUtils.mockOrderCreatedEventPayloadItem(RandomUtils.nextLong(1, 100), RandomUtils.nextInt(1, 10000));
        item1 = TestUtils.mockOrderCreatedEventPayloadItem(RandomUtils.nextLong(101, 200), RandomUtils.nextInt(1, 10000));


    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void handlePlaceOrderEvent_givenOrderQuantityGreaterThanInventoryQuantity_thenSendInventoryReservedFailedEvent() {
        var payload = OrderCreatedEventPayload.builder()
                .username("customer")
                .itemList(List.of(item, item1))
                .build();
        var appEvent = AppEvent.builder()
                .uuid(UUID.randomUUID().toString())
                .payload(payload)
                .build();

        var productIds = payload.getItemList().stream()
                .map(OrderCreatedEventPayload.Item::getProductId)
                .toList();
        var inventoryDto = TestUtils.mockInventoryDto(item.getProductId(), RandomUtils.nextLong(1, 100),
                RandomUtils.nextInt(0, item.getOrderQuantity() - 1),
                BigDecimal.valueOf(RandomUtils.nextDouble(1, 10000)));
        var inventoryDto1 = TestUtils.mockInventoryDto(item1.getProductId(), RandomUtils.nextLong(1, 100),
                RandomUtils.nextInt(item1.getOrderQuantity(), 10000),
                BigDecimal.valueOf(RandomUtils.nextDouble(1, 100000)));
        var inventoryDtoList = List.of(inventoryDto, inventoryDto1);

        var topicName = "inventory-reserved-failed";

        Mockito.when(modelMapper.map(appEvent.getPayload(), OrderCreatedEventPayload.class)).thenReturn(payload);
        Mockito.when(kafkaProducerProps.getInventoryReservedFailedTopicName()).thenReturn(topicName);
        Mockito.when(inventoryRepository.findAllInventoryByProductIdIn(productIds)).thenReturn(inventoryDtoList);

        inventoryService.handleOrderCreatedEvent(appEvent);

        Mockito.verify(inventoryRepository, Mockito.times(1)).findAllInventoryByProductIdIn(productIds);
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(Mockito.eq(topicName), Mockito.any(AppEvent.class));
    }

    @Test
    void handleOrderCreatedEvent_givenInventoryDtoNotFound_thenSendInventoryReservedFailedEvent() {
        var payload = OrderCreatedEventPayload.builder()
                .username("customer")
                .itemList(List.of(item, item1))
                .build();
        var appEvent = AppEvent.builder()
                .uuid(UUID.randomUUID().toString())
                .payload(payload)
                .build();

        var productIds = payload.getItemList().stream()
                .map(OrderCreatedEventPayload.Item::getProductId)
                .toList();
        var inventoryDto1 = TestUtils.mockInventoryDto(item1.getProductId(), RandomUtils.nextLong(1, 100),
                RandomUtils.nextInt(item1.getOrderQuantity(), 10000),
                BigDecimal.valueOf(RandomUtils.nextDouble(1, 100000)));

        var topicName = "inventory-reserved-failed";

        Mockito.when(modelMapper.map(appEvent.getPayload(), OrderCreatedEventPayload.class)).thenReturn(payload);
        Mockito.when(kafkaProducerProps.getInventoryReservedFailedTopicName()).thenReturn(topicName);
        Mockito.when(inventoryRepository.findAllInventoryByProductIdIn(productIds)).thenReturn(Collections.singletonList(inventoryDto1));

        inventoryService.handleOrderCreatedEvent(appEvent);

        Mockito.verify(inventoryRepository, Mockito.times(1)).findAllInventoryByProductIdIn(productIds);
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(Mockito.eq(topicName), Mockito.any(AppEvent.class));
    }

    @Test
    void handleOrderCreatedEvent_thenSendInventoryReservedEvent() {
        var payload = OrderCreatedEventPayload.builder()
                .username("customer")
                .itemList(List.of(item, item1))
                .build();
        var appEvent = AppEvent.builder()
                .uuid(UUID.randomUUID().toString())
                .payload(payload)
                .build();

        var productIds = payload.getItemList().stream()
                .map(OrderCreatedEventPayload.Item::getProductId)
                .toList();
        var inventoryDto = TestUtils.mockInventoryDto(item.getProductId(), RandomUtils.nextLong(1, 100),
                RandomUtils.nextInt(item.getOrderQuantity(), 10000),
                BigDecimal.valueOf(RandomUtils.nextDouble(1, 10000)));
        var inventoryDto1 = TestUtils.mockInventoryDto(item1.getProductId(), RandomUtils.nextLong(1, 100),
                RandomUtils.nextInt(item1.getOrderQuantity(), 10000),
                BigDecimal.valueOf(RandomUtils.nextDouble(1, 100000)));
        var inventoryDtoList = List.of(inventoryDto, inventoryDto1);

        var topicName = "inventory-reserved";

        Mockito.when(modelMapper.map(appEvent.getPayload(), OrderCreatedEventPayload.class)).thenReturn(payload);
        Mockito.when(kafkaProducerProps.getInventoryReservedTopicName()).thenReturn(topicName);
        Mockito.when(inventoryRepository.findAllInventoryByProductIdIn(productIds)).thenReturn(inventoryDtoList);

        inventoryService.handleOrderCreatedEvent(appEvent);

        Mockito.verify(inventoryRepository, Mockito.times(1)).findAllInventoryByProductIdIn(productIds);
        Mockito.verify(kafkaTemplate, Mockito.times(1)).send(Mockito.eq(topicName), Mockito.any(AppEvent.class));
    }


}