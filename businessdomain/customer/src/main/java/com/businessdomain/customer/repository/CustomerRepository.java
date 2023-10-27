package com.businessdomain.customer.repository;

import com.businessdomain.customer.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    public Optional<Customer> findCustomerByCode(String code);
    public Optional<Customer> findCustomerByIban(String iban);
}
