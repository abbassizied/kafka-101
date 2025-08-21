package io.github.abbassizied.customer_service.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerProducer {
    private static final String TOPIC = "customer.events";

    private final KafkaTemplate<String, CustomerEvent> kafkaTemplate;

    public CustomerProducer(KafkaTemplate<String, CustomerEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(CustomerEvent event) {
        kafkaTemplate.send(TOPIC, event.getCustomerId().toString(), event);
    }
}
