package com.businessdomain.customer.controller;

import com.businessdomain.customer.repository.CustomerRepository;
import com.businessdomain.customer.service.ICustomerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.businessdomain.customer.entities.Customer;
import com.businessdomain.customer.entities.CustomerProduct;
import com.businessdomain.customer.models.CustomerDto;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    ICustomerService customerService;

    @GetMapping()
    public List<CustomerDto> findAll() {
        return customerService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> findById(@PathVariable Long id) {
        Optional<CustomerDto> customerDto = customerService.findById(id);
        if (customerDto.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(customerDto.get());
    }

    @PostMapping()
    public ResponseEntity<CustomerDto> create(@RequestBody Customer input) {
        return ResponseEntity.ok(customerService.create(input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        boolean deleted = customerService.delete(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerDto> update(@PathVariable Long id, @RequestBody Customer input) {
        Optional<CustomerDto> customerDto = customerService.update(id, input);
        return customerDto.isPresent() ? ResponseEntity.ok(customerDto.get()) 
        : ResponseEntity.notFound().build();
    }

    @GetMapping("/full")
    public ResponseEntity<CustomerDto> findByCode(@RequestParam String code) {
        Optional<CustomerDto> customerDto = customerService.findByCode(code);
        return customerDto.isPresent() ? ResponseEntity.ok(customerDto.get()) : ResponseEntity.notFound().build();
    }
}
