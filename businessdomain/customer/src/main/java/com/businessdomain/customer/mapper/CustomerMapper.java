package com.businessdomain.customer.mapper;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.businessdomain.customer.entities.Customer;
import com.businessdomain.customer.models.CustomerDto;

@Mapper
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper( CustomerMapper.class);
 
    CustomerDto customerToCustomerDto(Customer customer);
    List<CustomerDto> listCustomerToListCustomerDto(List<Customer> listCustomer);
    Customer customerDtoToCustomer(CustomerDto customerDTO);
}
