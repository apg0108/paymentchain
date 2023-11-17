package com.businessdomain.customer.models;

import java.util.List;

import lombok.Data;

@Data
public class CustomerDto {
    private Long id;
    private String code;
    private String phone;
    private String iban;
    private String surename;
    private String address;
    private List<CustomerProductDto> products;
    private List<?> transactions;
}
