package com.eric6166.inventory.config.kafka;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
public class KafkaProducerProps {

    @Value("${spring.kafka.producers.inventory-reserved-failed.topic-name}")
    String inventoryReservedFailedTopicName;

    @Value("${spring.kafka.producers.inventory-reserved.topic-name}")
    String inventoryReservedTopicName;


}
