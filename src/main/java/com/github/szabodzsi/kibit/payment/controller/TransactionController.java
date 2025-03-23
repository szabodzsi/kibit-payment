package com.github.szabodzsi.kibit.payment.controller;

import com.github.szabodzsi.kibit.payment.dto.TransactionCreateDTO;
import com.github.szabodzsi.kibit.payment.dto.TransactionDTO;
import com.github.szabodzsi.kibit.payment.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v0/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionCreateDTO transactionCreateDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionService.createTransaction(transactionCreateDTO));
    }
}
