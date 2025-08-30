package com.BlackstoneeIT.customer_management_service.controller;

import com.BlackstoneeIT.customer_management_service.dto.CreateCustomer;
import com.BlackstoneeIT.customer_management_service.dto.CustomerResponse;
import com.BlackstoneeIT.customer_management_service.services.CustomerServices;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer")

public class CustomerController {
private CustomerServices customerServices;
@Autowired
private KafkaTemplate<String, String> kafkaTemplate;

@Autowired
public CustomerController(CustomerServices customerServices) {
    this.customerServices = customerServices;

}
    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomer CreateCustomer) {
        CustomerResponse resp = customerServices.createCustomer(CreateCustomer);
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomer(@PathVariable long id){
    CustomerResponse resp = customerServices.getCustomerById(id);
    return new ResponseEntity<CustomerResponse>(resp, HttpStatus.OK);
}
@GetMapping("/test")
public void test(){

    kafkaTemplate.send("test_topic", "test");

    //publish
    //listen
}
    @GetMapping("/{customerId}/accounts")
    public ResponseEntity<String> createAccount(
            @PathVariable Long customerId,
            @RequestParam String accountType,
            @RequestParam(required = false, defaultValue = "0.00") BigDecimal initialBalance) {
       String requestId= UUID.randomUUID().toString();
        customerServices.requestAccountCreation(customerId, accountType, initialBalance,requestId);
        return ResponseEntity.accepted().body("Account creation request sent successfully your request id = "+requestId);
    }

}
