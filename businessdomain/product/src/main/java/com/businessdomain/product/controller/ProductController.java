package com.businessdomain.product.controller;

import com.businessdomain.product.entities.Product;
import com.businessdomain.product.model.ProductDto;
import com.businessdomain.product.repository.ProductRepository;
import com.businessdomain.product.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {

    //@Value("${user.role}")
    //private String role;
    @Autowired
    private ProductService productService;

    @GetMapping()
    public List<ProductDto> findAll() {
        return productService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findById(@PathVariable Long id) {
        Optional<ProductDto> product = productService.findById(id);
        return product.isPresent() ? ResponseEntity.ok(product.get()) : ResponseEntity.notFound().build();
    }

    @PostMapping()
    public void create(@RequestBody Product input) {
        productService.create(input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        boolean deleted = productService.delete(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id, @RequestBody Product input) {
        Optional<ProductDto> productDto = productService.update(id, input);
        return productDto.isPresent() ? ResponseEntity.ok(productDto.get())
        : ResponseEntity.notFound().build();
    }
}
