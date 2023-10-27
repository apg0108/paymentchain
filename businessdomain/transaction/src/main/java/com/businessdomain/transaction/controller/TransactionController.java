package com.businessdomain.transaction.controller;

import com.businessdomain.transaction.entities.Status;
import com.businessdomain.transaction.entities.Transaction;
import com.businessdomain.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/transaction")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @GetMapping()
    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    @PostMapping()
    public ResponseEntity<Transaction> create(@RequestBody Transaction input) {
        Transaction data = validations(input);
        if (data == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(transactionRepository.save(data));
    }

    @GetMapping("/iba")
    public ResponseEntity<Transaction> findByAccountIban(@RequestParam String iba) {
        Optional<Transaction> transaction = transactionRepository.findTransactionByAccountIban(iba);
        return transaction.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(@PathVariable Long id, @RequestBody Transaction input) {
        Optional<Transaction> find = transactionRepository.findById(id);
        if (find.isPresent()) {
            Transaction transaction = find.get();
            transaction.setFee(input.getFee());
            transaction.setDate(input.getDate());
            transaction.setChannel(input.getChannel());
            transaction.setAmount(input.getAmount());
            transaction.setDescription(input.getDescription());
            transaction.setReference(input.getReference());
            transaction.setStatus(input.getStatus());
            transaction.setAccountIban(input.getAccountIban());

            Transaction data = validations(transaction);
            if (data == null) return ResponseEntity.badRequest().build();
            return ResponseEntity.ok(transactionRepository.save(data));
        }
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Transaction> delete(@PathVariable long id) {
        Optional<Transaction> findById = transactionRepository.findById(id);
        if (findById.isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    private Transaction validations(Transaction transaction) {
        if (transaction.getAmount() == 0) return null;
        if (transaction.getFee() > 0)
            transaction.setAmount(transaction.getAmount() - transaction.getFee());
        if (transaction.getDate().isAfter(LocalDate.now()))
            transaction.setStatus(Status.Pendiente);
        if (transaction.getDate().isBefore(LocalDate.now()) || transaction.getDate().equals(LocalDate.now()))
            transaction.setStatus(Status.Liquidada);
        return transaction;
    }
}
