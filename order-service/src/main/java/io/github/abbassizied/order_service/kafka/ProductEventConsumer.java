// src/main/java/io/github/abbassizied/order_service/kafka/ProductEventConsumer.java
package io.github.abbassizied.order_service.kafka;

import io.github.abbassizied.order_service.domain.ProductReplica;
import io.github.abbassizied.order_service.repos.ProductReplicaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@SuppressWarnings("java:S1068") // Suppress SonarQube unused field warning
public class ProductEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductEventConsumer.class);
    private final ProductReplicaRepository repository;
    @SuppressWarnings("unused") // Suppress IDE warning
    private final KafkaTopicsConfig topicsConfig;

    public ProductEventConsumer(ProductReplicaRepository repository, KafkaTopicsConfig topicsConfig) {
        this.repository = repository;
        this.topicsConfig = topicsConfig;
    }

    @Transactional
    @KafkaListener(
        topics = "#{@kafkaTopicsConfig.productEvents}", // SpEL expression
        groupId = "order-service-product",  // ← This sets the group ID
        // Specific factory
        containerFactory = "productKafkaListenerContainerFactory")
    public void onMessage(ProductEvent event) {
        if (event == null || event.getProductId() == null) {
            log.warn("Received invalid ProductEvent: {}", event);
            return;
        }

        switch (String.valueOf(event.getEventType())) {
            case "CREATED", "UPDATED": {
                ProductReplica replica = repository.findById(event.getProductId())
                        .orElseGet(ProductReplica::new);
                // assign ID if new
                if (replica.getId() == null) {
                    replica.setId(event.getProductId());
                }
                replica.setName(event.getName());
                replica.setQuantity(event.getQuantity());
                replica.setPrice(event.getPrice());
                repository.save(replica);
                log.info("Upserted ProductReplica id={}", replica.getId());
                break;
            }
            case "DELETED": {
                repository.deleteById(event.getProductId());
                log.info("Deleted ProductReplica id={}", event.getProductId());
                break;
            }
            default:
                log.warn("Unknown ProductEvent type: {}", event.getEventType());
        }
    }
}
