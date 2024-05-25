package com.eric6166.inventory.service.impl;

import com.eric6166.common.config.kafka.AppEvent;
import com.eric6166.inventory.config.kafka.KafkaProducerProps;
import com.eric6166.inventory.dto.InventoryCheckedEventPayload;
import com.eric6166.inventory.dto.ItemNotAvailableEventPayload;
import com.eric6166.inventory.dto.PlaceOrderEventPayload;
import com.eric6166.inventory.repository.InventoryRepository;
import com.eric6166.inventory.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryServiceImpl implements InventoryService {

    InventoryRepository inventoryRepository;
    ModelMapper modelMapper;
    KafkaTemplate<String, Object> kafkaTemplate;
    KafkaProducerProps kafkaProducerProps;

    @Transactional
    @Override
    public void handlePlaceOrderEvent(AppEvent appEvent) {
        var payload = modelMapper.map(appEvent.getPayload(), PlaceOrderEventPayload.class);
        var inventoryDtoList = inventoryRepository.findAllInventoryByProductIdIn(payload.getItemList().stream()
                .map(PlaceOrderEventPayload.Item::getProductId)
                .toList());
        List<PlaceOrderEventPayload.Item> placeOrderItemList = payload.getItemList();
        List<ItemNotAvailableEventPayload.Item> notAvailableItemList = new ArrayList<>();
        for (var placeOrderItem : placeOrderItemList) {
            var inventoryOpt = inventoryDtoList.stream().filter(dto -> dto.getProductId().equals(placeOrderItem.getProductId())).findFirst();
            if (inventoryOpt.isEmpty() || inventoryOpt.get().getInventoryQuantity() < placeOrderItem.getOrderQuantity()) {
                notAvailableItemList.add(ItemNotAvailableEventPayload.Item.builder()
                        .productId(placeOrderItem.getProductId())
                        .inventoryQuantity(inventoryOpt.isEmpty() ? null : inventoryOpt.get().getInventoryQuantity())
                        .orderQuantity(placeOrderItem.getOrderQuantity())
                        .build());
            }
        }
        if (CollectionUtils.isNotEmpty(notAvailableItemList)) {
            var itemNotAvailableEvent = AppEvent.builder()
                    .uuid(UUID.randomUUID().toString())
                    .payload(ItemNotAvailableEventPayload.builder()
                            .orderUuid(payload.getOrderUuid())
                            .username(payload.getUsername())
                            .itemList(notAvailableItemList)
                            .build())
                    .build();
            kafkaTemplate.send(kafkaProducerProps.getItemNotAvailableTopicName(), itemNotAvailableEvent);
            log.info("itemNotAvailableEvent sent :{}", itemNotAvailableEvent);
            return;
        }
        List<InventoryCheckedEventPayload.Item> inventoryCheckedItemList = new ArrayList<>();
        for (var placeOrderItem : placeOrderItemList) {
            var inventoryOpt = inventoryDtoList.stream().filter(dto -> dto.getProductId().equals(placeOrderItem.getProductId())).findFirst();
            inventoryOpt.ifPresent(inventoryDto -> inventoryCheckedItemList.add(InventoryCheckedEventPayload.Item.builder()
                    .productId(placeOrderItem.getProductId())
                    .orderQuantity(placeOrderItem.getOrderQuantity())
                    .productPrice(inventoryDto.getProductPrice())
                    .build()));
        }
        if (CollectionUtils.isNotEmpty(inventoryCheckedItemList)) {
            var inventoryCheckedEvent = AppEvent.builder()
                    .uuid(UUID.randomUUID().toString())
                    .payload(InventoryCheckedEventPayload.builder()
                            .orderUuid(payload.getOrderUuid())
                            .username(payload.getUsername())
                            .itemList(inventoryCheckedItemList)
                            .build())
                    .build();
            kafkaTemplate.send(kafkaProducerProps.getInventoryCheckedTopicName(), inventoryCheckedEvent);
            log.info("inventoryCheckedEvent sent :{}", inventoryCheckedEvent);
        }


    }

}
