package com.github.szabodzsi.kibit.payment.service;

import com.github.szabodzsi.kibit.payment.dto.TransactionCreateDTO;
import com.github.szabodzsi.kibit.payment.dto.TransactionDTO;

public interface TransactionService {
    TransactionDTO createTransaction(final TransactionCreateDTO transactionCreateDTO);
}
