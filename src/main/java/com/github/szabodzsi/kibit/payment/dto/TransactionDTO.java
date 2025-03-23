package com.github.szabodzsi.kibit.payment.dto;

import com.github.szabodzsi.kibit.payment.model.TransactionStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransactionDTO {
    private UUID id;

    private UUID clientTransactionId;

    private UUID sender;

    private UUID recipient;

    private BigDecimal amount;

    private TransactionStatus status;

    private LocalDateTime created;

    private LocalDateTime updated;
}
