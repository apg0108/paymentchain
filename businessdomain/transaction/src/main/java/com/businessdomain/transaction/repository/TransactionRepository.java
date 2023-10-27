package com.businessdomain.transaction.repository;

import com.businessdomain.transaction.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    public Optional<Transaction> findTransactionByAccountIban(String accountIban);
}
