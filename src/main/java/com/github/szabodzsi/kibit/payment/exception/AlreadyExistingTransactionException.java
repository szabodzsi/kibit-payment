package com.github.szabodzsi.kibit.payment.exception;

public class AlreadyExistingTransactionException extends RuntimeException {
    public AlreadyExistingTransactionException(String message) {
        super(message);
    }
}
