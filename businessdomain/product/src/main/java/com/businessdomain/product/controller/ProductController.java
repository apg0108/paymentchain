package com.businessdomain.product.controller;

import com.businessdomain.product.entities.Product;
import com.businessdomain.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/product")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    @GetMapping()
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        return product.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public void create(@RequestBody Product input) {
        productRepository.save(input);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> delete(@PathVariable Long id) {
        Optional<Product> customer = productRepository.findById(id);
        if (customer.isEmpty()) return ResponseEntity.notFound().build();
        productRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product input) {
        Optional<Product> find = productRepository.findById(id);
        if(find.isPresent()){
            Product product = find.get();
            product.setCode(input.getCode());
            product.setName(input.getName());
            return ResponseEntity.ok(productRepository.save(product));
        }
        return ResponseEntity.badRequest().build();
    }
}
