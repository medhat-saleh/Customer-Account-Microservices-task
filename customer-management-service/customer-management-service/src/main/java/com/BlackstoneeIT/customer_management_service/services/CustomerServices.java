package com.BlackstoneeIT.customer_management_service.services;

import com.BlackstoneeIT.customer_management_service.dto.CreateCustomer;
import com.BlackstoneeIT.customer_management_service.dto.CustomerResponse;
import com.BlackstoneeIT.customer_management_service.entity.Customer;
import com.BlackstoneeIT.customer_management_service.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerServices {
private CustomerRepository customerRepository;
private final CustomerIdGeneratorService idGeneratorService;

    private final KafkaProducerService kafkaProducerService ;
    private static final String ACCOUNT_CREATION_TOPIC = "account-creation-requests";
public CustomerResponse getCustomerById(long customerId) {
    Customer customer = (Customer) customerRepository.getCustomersById(customerId);

    return new CustomerResponse(
            customer.getId().toString(),
            customer.getLegalID(),
            customer.getFirstName(),
            customer.getLastName(),
            customer.getEmail(),
            customer.getPhone(),  // Assuming phone field exists
            customer.getAddress(),
            customer.getType()
    );
};
    @Transactional
public CustomerResponse createCustomer(CreateCustomer createCustomer) {
        Long customerId = idGeneratorService.generateUniqueCustomerId();
        Customer customer = Customer.builder()
                .id(customerId)
                .legalID(createCustomer.legalID())
                .firstName(createCustomer.firstName())
                .lastName(createCustomer.lastName())
                .email(createCustomer.email())
                .address(createCustomer.address())
                .phone(createCustomer.phoneNumber())
                .Type(createCustomer.customerType())
                .build();
        //Customer customer = modelMapper.map(createCustomer,Customer.class);
        Customer createdCustomer = customerRepository.save(customer);

        return new CustomerResponse(
                createdCustomer.getId().toString(),
                createdCustomer.getLegalID(),
                createdCustomer.getFirstName(),
                createdCustomer.getLastName(),
                createdCustomer.getEmail(),
                createdCustomer.getPhone(),
                createdCustomer.getAddress(),
                createdCustomer.getType()
        );
    }

    @KafkaListener(topics = "test_topic")
    public void listen(String message) {

        log.info("Received order event message: {}", message);

}
    @Transactional
    public void requestAccountCreation(Long customerId, String accountType, BigDecimal initialBalance,String requestId) {
        log.info("Requesting account creation for customer: {}, type: {}", customerId, accountType);

        // Verify customer exists and get customer type
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        // Publish account creation event to Kafka

 kafkaProducerService.sendAccountCreationEvent(
                customerId,
                customer.getType(),
                accountType,
                initialBalance != null ? initialBalance : BigDecimal.ZERO,
                requestId
        ).thenAccept(result -> {
            log.info("Account creation event published for customer: {}", customerId);
        }).exceptionally(ex -> {
            log.error("Failed to publish account creation event for customer: {}", customerId, ex);
            throw new RuntimeException("Failed to publish account creation event", ex);
        });

    }

}
