package com.eric6166.inventory.service.impl;

import com.eric6166.common.config.kafka.AppEvent;
import com.eric6166.inventory.config.kafka.KafkaProducerProps;
import com.eric6166.inventory.dto.InventoryReservedEventPayload;
import com.eric6166.inventory.dto.InventoryReservedFailedEventPayload;
import com.eric6166.inventory.dto.OrderCreatedEventPayload;
import com.eric6166.inventory.repository.InventoryRepository;
import com.eric6166.inventory.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ModelMapper modelMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaProducerProps kafkaProducerProps;

    @Transactional
    @Override
    public void handleOrderCreatedEvent(AppEvent appEvent) {
        var payload = modelMapper.map(appEvent.getPayload(), OrderCreatedEventPayload.class);
        var inventoryDtoList = inventoryRepository.findAllInventoryByProductIdIn(payload.getItemList().stream()
                .map(OrderCreatedEventPayload.Item::getProductId)
                .toList());
        var orderCreatedItemList = payload.getItemList();
        List<InventoryReservedFailedEventPayload.Item> inventoryReservedFailedItemList = new ArrayList<>();
        orderCreatedItemList.forEach(orderCreatedItem -> {
            var inventoryOpt = inventoryDtoList.stream()
                    .filter(dto -> dto.getProductId().equals(orderCreatedItem.getProductId()))
                    .findFirst();
            if (inventoryOpt.isEmpty() || inventoryOpt.get().getInventoryQuantity() < orderCreatedItem.getOrderQuantity()) {
                inventoryReservedFailedItemList.add(InventoryReservedFailedEventPayload.Item.builder()
                        .productId(orderCreatedItem.getProductId())
                        .inventoryQuantity(inventoryOpt.isEmpty() ? null : inventoryOpt.get().getInventoryQuantity())
                        .orderQuantity(orderCreatedItem.getOrderQuantity())
                        .build());
            }
        });
        if (CollectionUtils.isNotEmpty(inventoryReservedFailedItemList)) {
            var inventoryReservedFailedEvent = AppEvent.builder()
                    .uuid(UUID.randomUUID().toString())
                    .payload(InventoryReservedFailedEventPayload.builder()
                            .orderUuid(payload.getOrderUuid())
                            .orderDate(payload.getOrderDate())
                            .username(payload.getUsername())
                            .itemList(inventoryReservedFailedItemList)
                            .build())
                    .build();
            kafkaTemplate.send(kafkaProducerProps.getInventoryReservedFailedTopicName(), inventoryReservedFailedEvent);
            log.info("inventoryReservedFailedEvent sent :{}", inventoryReservedFailedEvent.getUuid());
            return;
        }
        List<InventoryReservedEventPayload.Item> inventoryReservedItemList = new ArrayList<>();
        orderCreatedItemList.forEach(orderCreatedItem ->
                inventoryDtoList.stream()
                        .filter(dto -> dto.getProductId().equals(orderCreatedItem.getProductId()))
                        .findFirst()
                        .ifPresent(dto -> inventoryReservedItemList.add(
                                InventoryReservedEventPayload.Item.builder()
                                        .productId(orderCreatedItem.getProductId())
                                        .orderQuantity(orderCreatedItem.getOrderQuantity())
                                        .productPrice(dto.getProductPrice())
                                        .build())));

        if (CollectionUtils.isNotEmpty(inventoryReservedItemList)) {
            var inventoryReservedEvent = AppEvent.builder()
                    .uuid(UUID.randomUUID().toString())
                    .payload(InventoryReservedEventPayload.builder()
                            .orderUuid(payload.getOrderUuid())
                            .orderDate(payload.getOrderDate())
                            .username(payload.getUsername())
                            .itemList(inventoryReservedItemList)
                            .build())
                    .build();
            kafkaTemplate.send(kafkaProducerProps.getInventoryReservedTopicName(), inventoryReservedEvent);
            log.info("inventoryReservedEvent sent :{}", inventoryReservedEvent.getUuid());
        }


    }

}
