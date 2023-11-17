package com.businessdomain.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.businessdomain.product.entities.Product;
import com.businessdomain.product.mapper.ProductMapper;
import com.businessdomain.product.model.ProductDto;
import com.businessdomain.product.repository.ProductRepository;

public class ProductService implements IProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<ProductDto> findAll() {
        return productMapper.listProductToProductDto(productRepository.findAll());
    }

    @Override
    public Optional<ProductDto> findById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.isPresent() ? Optional.of(productMapper.productToProductDto(product.get()))
        : Optional.of(new ProductDto());
    }

    @Override
    public void create(Product input) {
        productRepository.save(input);
    }

    @Override
    public boolean delete(Long id) {
        Optional<Product> product = productRepository.findById(id);
        productRepository.deleteById(id);
        return product.isPresent();
    }

    @Override
    public Optional<ProductDto> update(Long id, Product input) {
        Optional<Product> find = productRepository.findById(id);
        if(find.isPresent()){
            Product product = find.get();
            product.setCode(input.getCode());
            product.setName(input.getName());
            return Optional.of(productMapper.productToProductDto(product));
        }
        return Optional.of(new ProductDto());
    }
    
}
