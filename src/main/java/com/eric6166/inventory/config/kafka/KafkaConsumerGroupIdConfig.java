package com.eric6166.inventory.config.kafka;

import com.eric6166.common.config.kafka.KafkaConsumerConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerGroupIdConfig {

    private final KafkaConsumerConfig kafkaConsumerConfig;

    private final KafkaConsumerProps kafkaConsumerProps;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> templateTopicKafkaListenerContainerFactory() {
        return kafkaConsumerConfig.kafkaListenerContainerFactory(kafkaConsumerProps.getTemplateGroupId());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> testTopicKafkaListenerContainerFactory() {
        return kafkaConsumerConfig.kafkaListenerContainerFactory(kafkaConsumerProps.getTestTopicGroupId());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> orderCreatedKafkaListenerContainerFactory() {
        return kafkaConsumerConfig.kafkaListenerContainerFactory(kafkaConsumerProps.getOrderCreatedGroupId());
    }

}
