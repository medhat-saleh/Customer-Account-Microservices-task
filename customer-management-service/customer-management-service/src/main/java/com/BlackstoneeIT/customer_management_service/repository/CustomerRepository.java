package com.BlackstoneeIT.customer_management_service.repository;

import com.BlackstoneeIT.customer_management_service.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Object getCustomersById(long id);
}
