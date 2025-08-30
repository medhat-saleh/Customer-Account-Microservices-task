package com.BlackstoneeIT.customer_management_service.entity;

import com.BlackstoneeIT.customer_management_service.enums.CustomerType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers",schema = "customer_db")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    @Column(name = "legal_id",nullable = false,unique = true)
   private String legalID;
    @Column(name = "first_name",nullable = false)
    private String firstName;
    @Column( name = "last_name")
    private String lastName;
    @Enumerated(EnumType.STRING)
    private CustomerType Type;
    @Email
    @Column(name = "email")
    private  String email;
    @Column(name = "addres")
   private String address;
    @Column(name = "phone")
    private String phone;
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        updatedDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }


}
