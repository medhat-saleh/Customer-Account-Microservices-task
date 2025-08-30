package com.BlackstoneeIT.customer_management_service.services;

import com.BlackstoneeIT.customer_management_service.dto.AccountCreationEvent;
import com.BlackstoneeIT.customer_management_service.dto.AccountCreationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaTopicReaderService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ConsumerFactory<String, AccountCreationResponse> consumerFactory;
    private final String responseTopic = "account-creation-responses";
    private final ConsumerFactory<String, AccountCreationResponse> accountResponseConsumerFactory;

    private Consumer<String, AccountCreationResponse> createConsumerFromFactory() {
        return accountResponseConsumerFactory.createConsumer(
                "account-service-group" + System.currentTimeMillis(),
                String.valueOf(System.currentTimeMillis())
        );
    }
    public AccountCreationResponse findResponseByRequestId(String requestId) {
        // Use KafkaTemplate's consumer factory to create a temporary consumer
        try (Consumer<String, AccountCreationResponse> consumer = createConsumerFromFactory()) {
            return findResponseWithConsumer(consumer, requestId);
        } catch (Exception e) {
            log.error("Error reading from Kafka topic: {}", e.getMessage());
            return null;
        }
    }


    private AccountCreationResponse findResponseWithConsumer(
            Consumer<String, AccountCreationResponse> consumer, String requestId) {

        consumer.subscribe(Collections.singletonList(responseTopic));

        // Poll for messages with a short timeout
        ConsumerRecords<String, AccountCreationResponse> records = consumer.poll(Duration.ofSeconds(3));
        return StreamSupport.stream(records.records(responseTopic).spliterator(), false)
                .filter(record -> requestId.equals(record.key()))
                .findFirst()
                .map(record -> record.value())
                .orElse(null);
    }

    public AccountCreationResponse findResponseByRequestIdWithSeek(String requestId) {
        try (Consumer<String, AccountCreationResponse> consumer = createConsumerFromFactory()) {

            // Get all partitions for the topic
            var partitions = consumer.partitionsFor(responseTopic);
            if (partitions == null) return null;

            for (var partitionInfo : partitions) {
                TopicPartition partition = new TopicPartition(responseTopic, partitionInfo.partition());
                consumer.assign(Collections.singletonList(partition));
                consumer.seekToBeginning(Collections.singletonList(partition));

                long endOffset = consumer.endOffsets(Collections.singletonList(partition)).get(partition);
                long currentPosition = consumer.position(partition);

                // Read until we find the message or reach the end
                while (currentPosition < endOffset) {
                    ConsumerRecords<String, AccountCreationResponse> records = consumer.poll(Duration.ofMillis(100));
                    if (records.isEmpty()) break;

                    for (var record : records.records(partition)) {
                        if (requestId.equals(record.key())) {
                            return record.value();
                        }
                    }
                    currentPosition = consumer.position(partition);
                }
            }
            return null;

        } catch (Exception e) {
            log.error("Error seeking in Kafka topic: {}", e.getMessage());
            return null;
        }
    }

}