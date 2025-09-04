package io.github.abbassizied.product_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ProductProducer {
    private final KafkaTopicsConfig kafkaTopicsConfig;

    private final KafkaTemplate<String, ProductEvent> kafkaTemplate;

    public ProductProducer(KafkaTemplate<String, ProductEvent> kafkaTemplate, KafkaTopicsConfig kafkaTopicsConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicsConfig = kafkaTopicsConfig;
    }

    public void sendEvent(ProductEvent event) {
        String productTopic = kafkaTopicsConfig.getProductEvents();
        System.out.println("Product topic: " + productTopic);
        kafkaTemplate.send(productTopic, event.getProductId().toString(), event);
    }
}
