package io.github.abbassizied.order_service.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    private Map<String, Object> baseConfig(String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        return props;
    }

    @Bean
    public ConsumerFactory<String, ProductEvent> productConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(baseConfig("order-service-product"),
                new StringDeserializer(),
                new JsonDeserializer<>(ProductEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductEvent> productKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProductEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(productConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, CustomerEvent> customerConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(baseConfig("order-service-customer"),
                new StringDeserializer(),
                new JsonDeserializer<>(CustomerEvent.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CustomerEvent> customerKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, CustomerEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(customerConsumerFactory());
        return factory;
    }
}
