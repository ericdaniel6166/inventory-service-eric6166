package com.eric6166.inventory.config.kafka;

import brave.Tracer;
import brave.propagation.TraceContextOrSamplingFlags;
import com.eric6166.base.exception.AppException;
import com.eric6166.common.config.kafka.AppEvent;
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

    Tracer tracer;

    @KafkaListener(topics = "${spring.kafka.consumers.test-topic.topic-name}",
            groupId = "${spring.kafka.consumers.test-topic.group-id}",
            containerFactory = "testTopicKafkaListenerContainerFactory",
            concurrency = "${spring.kafka.consumers.test-topic.properties.concurrency}"
    )
    public void handleTestTopicEvent(AppEvent appEvent) {
        var span = tracer.nextSpan().name("handleTestTopicEvent").start();
        try (var ws = tracer.withSpanInScope(span)) {
            span.tag("testTopicAppEvent uuid", appEvent.getUuid());
            log.info("handleTestTopicEvent, appEvent: {}", appEvent);
        } catch (RuntimeException e) {
            log.info("e: {} , errorMessage: {}", e.getClass().getName(), e.getMessage()); // comment // for local testing
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }

    }

    @KafkaListener(topics = "${spring.kafka.template.consumer.topic-name}",
            groupId = "${spring.kafka.template.consumer.group-id}",
            containerFactory = "templateTopicKafkaListenerContainerFactory",
            concurrency = "${spring.kafka.template.consumer.properties.concurrency}"
    )
    public void handleTemplateTopicEvent(AppEvent appEvent) {
        var span = tracer.nextSpan().name("handleTemplateTopicEvent").start();
        try (var ws = tracer.withSpanInScope(span)) {
            span.tag("templateTopicEvent uuid", appEvent.getUuid());
            log.info("handleTemplateTopicEvent, appEvent: {}", appEvent);
        } catch (RuntimeException e) {
            log.info("e: {} , errorMessage: {}", e.getClass().getName(), e.getMessage()); // comment // for local testing
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }

    }

}
