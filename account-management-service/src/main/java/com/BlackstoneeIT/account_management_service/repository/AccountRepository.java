package com.BlackstoneeIT.account_management_service.repository;

import com.BlackstoneeIT.account_management_service.entity.Account;
import com.BlackstoneeIT.account_management_service.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByCustomerId(Long customerId);

    long countByCustomerId(Long customerId);

    boolean existsByCustomerIdAndType(Long customerId, AccountType type);

    Optional<Account> findByIdAndCustomerId(Long id, Long customerId);

    @Query("SELECT a FROM Account a WHERE a.customerId = :customerId AND a.type = 'SALARY'")
    Optional<Account> findSalaryAccountByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT COUNT(a) FROM Account a WHERE a.customerId = :customerId AND a.type = 'INVESTMENT'")
    long countInvestmentAccountsByCustomerId(@Param("customerId") Long customerId);
    // Check if account ID exists
    boolean existsById(Long accountId);

    // Find maximum account ID for a customer (for sequential generation)
    @Query("SELECT MAX(a.id) FROM Account a WHERE a.customerId = :customerId")
    Optional<Long> findMaxAccountIdByCustomerId(@Param("customerId") Long customerId);
}