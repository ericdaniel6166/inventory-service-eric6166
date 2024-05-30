package com.eric6166.inventory.config.kafka;

import com.eric6166.common.config.kafka.AppEvent;
import com.eric6166.inventory.service.InventoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class KafkaConsumer {

    InventoryService inventoryService;

    @KafkaListener(topics = "${spring.kafka.consumers.test-topic.topic-name}",
            groupId = "${spring.kafka.consumers.test-topic.group-id}",
            containerFactory = "testTopicKafkaListenerContainerFactory",
            concurrency = "${spring.kafka.consumers.test-topic.properties.concurrency}"
    )
    public void handleTestTopicEvent(AppEvent appEvent) {
        log.info("handleTestTopicEvent, appEvent: {}", appEvent);

    }

    @KafkaListener(topics = "${spring.kafka.consumers.place-order.topic-name}",
            groupId = "${spring.kafka.consumers.place-order.group-id}",
            containerFactory = "placeOrderKafkaListenerContainerFactory",
            concurrency = "${spring.kafka.consumers.place-order.properties.concurrency}"
    )
    public void handlePlaceOrderEvent(AppEvent appEvent) {
        log.info("handlePlaceOrderEvent, appEvent: {}", appEvent);
        inventoryService.handlePlaceOrderEvent(appEvent);

    }

    @KafkaListener(topics = "${spring.kafka.template.consumer.topic-name}",
            groupId = "${spring.kafka.template.consumer.group-id}",
            containerFactory = "templateTopicKafkaListenerContainerFactory",
            concurrency = "${spring.kafka.template.consumer.properties.concurrency}"
    )
    public void handleTemplateTopicEvent(AppEvent appEvent) {
        log.info("handleTemplateTopicEvent, appEvent: {}", appEvent);

    }

}
