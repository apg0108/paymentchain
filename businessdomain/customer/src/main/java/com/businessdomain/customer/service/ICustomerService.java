package com.businessdomain.customer.service;

import java.util.List;
import java.util.Optional;

import com.businessdomain.customer.entities.Customer;
import com.businessdomain.customer.models.CustomerDto;

public interface ICustomerService {

    List<CustomerDto> findAll();
    Optional<CustomerDto> findById(Long id);
    CustomerDto create(Customer input);
    boolean delete(Long id);
    Optional<CustomerDto> update(Long id, Customer input);
    Optional<CustomerDto> findByCode(String code);
}
