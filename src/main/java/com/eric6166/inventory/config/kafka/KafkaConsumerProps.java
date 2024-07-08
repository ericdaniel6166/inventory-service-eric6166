package com.eric6166.inventory.config.kafka;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConditionalOnProperty(name = "spring.kafka.enabled", havingValue = "true")
public class KafkaConsumerProps {

    @Value("${spring.kafka.consumers.test-topic.group-id}")
    private String testTopicGroupId;

    @Value("${spring.kafka.consumers.order-created.group-id}")
    private String orderCreatedGroupId;

    @Value("${spring.kafka.template.consumer.group-id}")
    private String templateGroupId;

}
