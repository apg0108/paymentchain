package com.businessdomain.customer.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.businessdomain.customer.entities.Customer;
import com.businessdomain.customer.entities.CustomerProduct;
import com.businessdomain.customer.mapper.CustomerMapper;
import com.businessdomain.customer.models.CustomerDto;
import com.businessdomain.customer.repository.CustomerRepository;
import com.fasterxml.jackson.databind.JsonNode;

import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import reactor.netty.http.client.HttpClient;

@Service
public class CustomerService implements ICustomerService {

    private final WebClient.Builder webClientBuilder;

    public CustomerService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    HttpClient client = HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(EpollChannelOption.TCP_KEEPIDLE, 300)
            .option(EpollChannelOption.TCP_KEEPINTVL, 60)
            .responseTimeout(Duration.ofSeconds(1))
            .doOnConnected(connection -> {
                connection.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS));
                connection.addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS));
            });

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<CustomerDto> findAll() {
        return customerMapper.listCustomerToListCustomerDto(customerRepository.findAll());
    }

    @Override
    public Optional<CustomerDto> findById(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.isPresent() ? Optional.of(customerMapper.customerToCustomerDto(customer.get()))
                : Optional.of(new CustomerDto());
    }

    @Override
    public CustomerDto create(Customer input) {
        input.getProducts().forEach(x -> x.setCustomer(input));
        return customerMapper.customerToCustomerDto(customerRepository.save(input));
    }

    @Override
    public boolean delete(Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        customerRepository.deleteById(id);
        return customer.isPresent();
    }

    @Override
    public Optional<CustomerDto> update(Long id, Customer input) {
        Optional<Customer> find = customerRepository.findById(id);
        if (find.isPresent()) {
            Customer customer = find.get();
            customer.setCode(input.getCode());
            customer.setIban(input.getIban());
            customer.setPhone(input.getPhone());
            customer.setSurename(input.getSurename());
            return Optional.of(customerMapper.customerToCustomerDto(customer));
        }
        return Optional.of(new CustomerDto());
    }

    @Override
    public Optional<CustomerDto> findByCode(String code) {
        Optional<Customer> customer = customerRepository.findCustomerByCode(code);
        if (customer.isPresent()) {
            Customer dataCustomer = customer.get();
            List<CustomerProduct> products = dataCustomer.getProducts();
            products.forEach(p -> p.setProductName(getProductName(p.getId())));
            List<?> transactions = getTransactions(dataCustomer.getIban());
            dataCustomer.setTransactions(transactions);
            return Optional.of(customerMapper.customerToCustomerDto(dataCustomer));
        }
        return Optional.of(new CustomerDto());
    }

    private String getProductName(long id) {
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://localhost:8082/product")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", "http://localhost:8082/product"))
                .build();

        JsonNode block = build.method(HttpMethod.GET).uri("/" + id)
                .retrieve().bodyToMono(JsonNode.class).block();
        return block != null ? block.get("name").asText() : null;
    }

    private List<?> getTransactions(String iba) {
        WebClient build = webClientBuilder.clientConnector(new ReactorClientHttpConnector(client))
                .baseUrl("http://localhost:8083/transaction")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        return build.method(HttpMethod.GET).uri(uriBuilder -> uriBuilder.path("/iba")
                .queryParam("iba", iba)
                .build())
                .retrieve().bodyToFlux(Object.class).collectList().block();
    }
}
