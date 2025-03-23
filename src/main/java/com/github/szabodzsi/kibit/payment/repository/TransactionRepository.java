package com.github.szabodzsi.kibit.payment.repository;

import com.github.szabodzsi.kibit.payment.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Optional<Transaction> findTransactionByClientTransactionId(UUID clientTransactionId);
}
