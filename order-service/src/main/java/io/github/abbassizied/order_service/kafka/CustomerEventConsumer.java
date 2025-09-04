// src/main/java/io/github/abbassizied/order_service/kafka/CustomerEventConsumer.java
package io.github.abbassizied.order_service.kafka;

import io.github.abbassizied.order_service.domain.CustomerReplica;
import io.github.abbassizied.order_service.repos.CustomerReplicaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@SuppressWarnings("java:S1068") // Suppress SonarQube unused field warning
public class CustomerEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(CustomerEventConsumer.class);
    private final CustomerReplicaRepository repository;

    @SuppressWarnings("unused") // Suppress IDE warning
    private final KafkaTopicsConfig topicsConfig;

    public CustomerEventConsumer(CustomerReplicaRepository repository, KafkaTopicsConfig topicsConfig) {
        this.repository = repository;
        this.topicsConfig = topicsConfig;
    }

    @Transactional
    @KafkaListener(
        topics = "#{@kafkaTopicsConfig.customerEvents}", // SpEL expression
        groupId = "order-service-customer", // ← This sets the group ID
        // Specific factory
        containerFactory = "customerKafkaListenerContainerFactory")
    public void onMessage(CustomerEvent event) {
        if (event == null || event.getCustomerId() == null) {
            log.warn("Received invalid CustomerEvent: {}", event);
            return;
        }

        switch (String.valueOf(event.getEventType())) {
            case "CREATED", "UPDATED": {
                CustomerReplica replica = repository.findById(event.getCustomerId())
                        .orElseGet(CustomerReplica::new);
                if (replica.getId() == null) {
                    replica.setId(event.getCustomerId());
                }
                replica.setName(event.getName());
                replica.setEmail(event.getEmail());
                replica.setPhone(event.getPhone());
                replica.setShippingAddress(event.getShippingAddress());
                replica.setBillingAddress(event.getBillingAddress());
                repository.save(replica);
                log.info("Upserted CustomerReplica id={}", replica.getId());
                break;
            }
            case "DELETED": {
                repository.deleteById(event.getCustomerId());
                log.info("Deleted CustomerReplica id={}", event.getCustomerId());
                break;
            }
            default:
                log.warn("Unknown CustomerEvent type: {}", event.getEventType());
        }
    }
}
