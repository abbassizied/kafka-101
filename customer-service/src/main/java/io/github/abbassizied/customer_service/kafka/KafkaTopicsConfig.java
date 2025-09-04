package io.github.abbassizied.customer_service.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "app.kafka.topics")
@Getter
@Setter
public class KafkaTopicsConfig {
    private String customerEvents;
}
