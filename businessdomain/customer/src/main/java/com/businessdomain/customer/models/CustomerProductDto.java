package com.businessdomain.customer.models;
import lombok.Data;

@Data
public class CustomerProductDto {
    private long id;
    private long productId;
    private String productName;
    private CustomerDto customer;
}
