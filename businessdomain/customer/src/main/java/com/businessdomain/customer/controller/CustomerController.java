package com.businessdomain.customer.controller;

import com.businessdomain.customer.repository.CustomerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.businessdomain.customer.entities.Customer;
import com.businessdomain.customer.entities.CustomerProduct;
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
    private CustomerRepository customerRepository;

    private final WebClient.Builder webClientBuilder;

    public CustomerController(WebClient.Builder webClientBuilder) {
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

    @GetMapping()
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> findById(@PathVariable Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        return customer.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping()
    public ResponseEntity<Customer> create(@RequestBody Customer input) {
        input.getProducts().forEach(x -> x.setCustomer(input));
        return ResponseEntity.ok(customerRepository.save(input));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Customer> delete(@PathVariable Long id) {
        Optional<Customer> customer = customerRepository.findById(id);
        if (customer.isEmpty())
            return ResponseEntity.notFound().build();
        customerRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @RequestBody Customer input) {
        Optional<Customer> find = customerRepository.findById(id);
        if (find.isPresent()) {
            Customer customer = find.get();
            customer.setCode(input.getCode());
            customer.setIban(input.getIban());
            customer.setPhone(input.getPhone());
            customer.setSurename(input.getSurename());
            return ResponseEntity.ok(customerRepository.save(customer));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/full")
    public ResponseEntity<Customer> findByCode(@RequestParam String code) {
        Optional<Customer> customer = customerRepository.findCustomerByCode(code);
        if (customer.isEmpty())
            return ResponseEntity.notFound().build();
        Customer dataCustomer = customer.get();
        List<CustomerProduct> products = dataCustomer.getProducts();
        products.forEach(p -> p.setProductName(getProductName(p.getId())));
        List<?> transactions = getTransactions(dataCustomer.getIban());
        dataCustomer.setTransactions(transactions);
        return ResponseEntity.ok(dataCustomer);
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
