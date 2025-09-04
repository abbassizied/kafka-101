package io.github.abbassizied.customer_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerProducer {
    private final KafkaTopicsConfig kafkaTopicsConfig;

    private final KafkaTemplate<String, CustomerEvent> kafkaTemplate;

    public CustomerProducer(KafkaTemplate<String, CustomerEvent> kafkaTemplate, KafkaTopicsConfig kafkaTopicsConfig) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicsConfig = kafkaTopicsConfig;
    }

    public void sendEvent(CustomerEvent event) {
        String customerTopic = kafkaTopicsConfig.getCustomerEvents();
        System.out.println("Customer topic: " + customerTopic);
        kafkaTemplate.send(customerTopic, event.getCustomerId().toString(), event);
    }
}
