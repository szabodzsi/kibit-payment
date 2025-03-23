package com.github.szabodzsi.kibit.payment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransactionCreateDTO {
    private UUID clientTransactionId;
    private UUID sender;
    private UUID recipient;
    private BigDecimal amount;
}
