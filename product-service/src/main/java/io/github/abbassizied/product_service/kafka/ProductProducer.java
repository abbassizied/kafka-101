package io.github.abbassizied.product_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductProducer {
    private static final String TOPIC = "product.events";

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public ProductProducer(KafkaTemplate<String, ProductEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(ProductEvent event) {
        kafkaTemplate.send(TOPIC, event.getProductId().toString(), event);
    }
}
