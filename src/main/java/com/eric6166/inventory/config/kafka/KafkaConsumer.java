package com.eric6166.inventory.config.kafka;

import brave.Span;
import brave.Tracer;
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
    public void handleTestTopicEvent(AppEvent appEvent) throws AppException {
        Span span = tracer.nextSpan().name("handleTestTopicEvent").start();
        try (var ws = tracer.withSpanInScope(span)) {
            span.tag("testTopicAppEvent uuid", appEvent.getUuid());
            log.debug("handleTestTopicEvent, appEvent: {}", appEvent);
        } catch (RuntimeException e) {
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }

    }

    @KafkaListener(topics = "${spring.kafka.template.default-topic}",
            groupId = "${spring.kafka.template.default-group-id}",
            containerFactory = "defaultTopicKafkaListenerContainerFactory",
            concurrency = "${spring.kafka.template.properties.default-concurrency}"
    )
    public void handleDefaultTopicEvent(AppEvent appEvent) throws AppException {
        Span span = tracer.nextSpan().name("handleDefaultTopicEvent").start();
        try (var ws = tracer.withSpanInScope(span)) {
            span.tag("defaultTopicEvent uuid", appEvent.getUuid());
            log.debug("handleDefaultTopicEvent, appEvent: {}", appEvent);
        } catch (RuntimeException e) {
            span.error(e);
            throw e;
        } finally {
            span.finish();
        }

    }

}
