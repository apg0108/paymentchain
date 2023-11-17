package com.businessdomain.product.service;

import java.util.List;
import java.util.Optional;

import com.businessdomain.product.entities.Product;
import com.businessdomain.product.model.ProductDto;

public interface IProductService {
    List<ProductDto> findAll();
    Optional<ProductDto> findById(Long id);
    void create(Product input);
    boolean delete(Long id);
    Optional<ProductDto> update(Long id, Product input);
}
