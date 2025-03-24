package com.github.szabodzsi.kibit.payment.service;

import com.github.szabodzsi.kibit.payment.dto.TransactionCreateDTO;
import com.github.szabodzsi.kibit.payment.dto.TransactionDTO;
import com.github.szabodzsi.kibit.payment.exception.AlreadyExistingTransactionException;
import com.github.szabodzsi.kibit.payment.exception.InsufficientBalanceException;
import com.github.szabodzsi.kibit.payment.exception.UserIdNotFoundException;
import com.github.szabodzsi.kibit.payment.model.Account;
import com.github.szabodzsi.kibit.payment.model.Transaction;
import com.github.szabodzsi.kibit.payment.model.TransactionStatus;
import com.github.szabodzsi.kibit.payment.repository.AccountRepository;
import com.github.szabodzsi.kibit.payment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    private final AccountRepository accountRepository;

    private final ModelMapper modelMapper;

    private final TransactionNotificationService transactionNotificationService;

    @Override
    public TransactionDTO createTransaction(TransactionCreateDTO transactionCreateDTO) {
        validateClientTransactionId(transactionCreateDTO.getClientTransactionId());
        var senderAccount = accountRepository.findAccountByUserId(transactionCreateDTO.getSender());
        validateSenderAccountAndBalance(senderAccount, transactionCreateDTO);
        var recipientAccount = accountRepository.findAccountByUserId(transactionCreateDTO.getRecipient());
        validateRecipientAccount(recipientAccount, transactionCreateDTO);

        Transaction transaction = modelMapper.map(transactionCreateDTO, Transaction.class);
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setCreated(LocalDateTime.now());
        transaction.setUpdated(LocalDateTime.now());
        Transaction created = transactionRepository.save(transaction);

        updateAccounts(senderAccount.orElseThrow(), recipientAccount.orElseThrow(), transactionCreateDTO.getAmount());

        transactionNotificationService.sendNotification("Transaction wih id " + created.getId() + " sent.");

        transaction.setUpdated(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.COMPLETED);
        transactionRepository.save(transaction);

        TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);
        log.info("Transaction created:" + transactionDTO);
        return transactionDTO;
    }

    private void validateClientTransactionId(UUID clientTransactionId) {
        if (transactionRepository.findTransactionByClientTransactionId(clientTransactionId)
                .isPresent() ) {
            throw new AlreadyExistingTransactionException("Client transaction id already exists: " + clientTransactionId);
        }
    }

    private void validateSenderAccountAndBalance(Optional<Account> sender, TransactionCreateDTO transactionCreateDTO) {
        if (sender.isEmpty()) {
            throw new UserIdNotFoundException("Account for sender id not found: " + transactionCreateDTO.getSender());
        }
        if (sender.get().getBalance().compareTo(transactionCreateDTO.getAmount()) != 1) {
            throw new InsufficientBalanceException("Insufficient balance for sending amount: " + transactionCreateDTO.getAmount());
        }
    }

    private void validateRecipientAccount(Optional<Account> recipient, TransactionCreateDTO transactionCreateDTO) {
        if (recipient.isEmpty()) {
            throw new UserIdNotFoundException("Account for recipient id not found: " + transactionCreateDTO.getRecipient());
        }
    }

    private void updateAccounts(Account sender, Account recipient, BigDecimal amount) {
        sender.setBalance(sender.getBalance().add(amount.negate()));
        accountRepository.save(sender);
        recipient.setBalance(recipient.getBalance().add(amount));
        accountRepository.save(recipient);
    }
}
