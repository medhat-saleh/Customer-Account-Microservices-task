package com.BlackstoneeIT.customer_management_service.services;

import com.BlackstoneeIT.customer_management_service.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerIdGeneratorService {

    private final CustomerRepository customerRepository;
    private final Random random = new Random();

    public Long generateUniqueCustomerId() {
        Long customerId;
        int attempts = 0;
        final int maxAttempts = 20;

        do {
            // Generate random 7-digit number
            customerId = generateRandomCustomerId();

            // Check if ID already exists
            if (!customerRepository.existsById(customerId)) {
                log.debug("Generated unique customer ID: {}", customerId);
                return customerId;
            }

            log.warn("Customer ID {} already exists, attempt {}/{}",
                    customerId, ++attempts, maxAttempts);

        } while (attempts < maxAttempts);

        throw new IllegalStateException(
                "Failed to generate unique customer ID after " + maxAttempts + " attempts");
    }

    private Long generateRandomCustomerId() {
        return 1000000L + (long) (random.nextDouble() * 9000000L);
    }

//    // Alternative: Database sequence-based generation
//    public Long generateSequentialCustomerId() {
//        Long maxId = customerRepository.findMaxId().orElse(1000000L);
//        Long nextId = maxId + 1;
//
//        // Ensure it's within 7-digit range
//        if (nextId > 9999999L) {
//            throw new IllegalStateException("Customer ID range exhausted");
//        }
//
//        return nextId;
//    }
}