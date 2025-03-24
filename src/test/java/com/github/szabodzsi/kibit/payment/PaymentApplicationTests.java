package com.github.szabodzsi.kibit.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.szabodzsi.kibit.payment.dto.TransactionCreateDTO;
import com.github.szabodzsi.kibit.payment.service.TransactionNotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class PaymentApplicationTests {

	@MockitoBean
	private TransactionNotificationService transactionNotificationService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@Test
	void contextLoads() {
	}

	@Test
	void shouldCreateTransaction() throws Exception {
		TransactionCreateDTO transactionCreateDTO = new TransactionCreateDTO();
		transactionCreateDTO.setClientTransactionId(UUID.randomUUID());
		transactionCreateDTO.setAmount(BigDecimal.valueOf(100));
		transactionCreateDTO.setSender(UUID.fromString("5f8bae39-4d39-40cb-9c2f-f5aacd434d07"));
		transactionCreateDTO.setRecipient(UUID.fromString("e46eaa3c-ae54-4914-87a2-cdd860c51594"));

		objectMapper.registerModule(new JavaTimeModule());
		String json = objectMapper.writeValueAsString(transactionCreateDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v0/transaction" )
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(json))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("id").exists());

		verify(transactionNotificationService).sendNotification(anyString());
	}

	@Test
	void shouldNotCreateTransactionDueToInsufficientBalance() throws Exception {
		TransactionCreateDTO transactionCreateDTO = new TransactionCreateDTO();
		transactionCreateDTO.setClientTransactionId(UUID.randomUUID());
		transactionCreateDTO.setAmount(BigDecimal.valueOf(10000));
		transactionCreateDTO.setSender(UUID.fromString("5f8bae39-4d39-40cb-9c2f-f5aacd434d07"));
		transactionCreateDTO.setRecipient(UUID.fromString("e46eaa3c-ae54-4914-87a2-cdd860c51594"));

		objectMapper.registerModule(new JavaTimeModule());
		String json = objectMapper.writeValueAsString(transactionCreateDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v0/transaction" )
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(json))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Insufficient balance for sending amount: 10000"));

		verify(transactionNotificationService, never()).sendNotification(anyString());
	}

	@Test
	void shouldNotCreateTransactionDueToNotExistingSenderId() throws Exception {
		TransactionCreateDTO transactionCreateDTO = new TransactionCreateDTO();
		transactionCreateDTO.setClientTransactionId(UUID.randomUUID());
		transactionCreateDTO.setAmount(BigDecimal.valueOf(10000));
		transactionCreateDTO.setSender(UUID.fromString("a7b1c77c-b577-4381-aea8-394026d8eafc"));
		transactionCreateDTO.setRecipient(UUID.fromString("e46eaa3c-ae54-4914-87a2-cdd860c51594"));

		objectMapper.registerModule(new JavaTimeModule());
		String json = objectMapper.writeValueAsString(transactionCreateDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v0/transaction" )
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(json))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Account for sender id not found: a7b1c77c-b577-4381-aea8-394026d8eafc"));

		verify(transactionNotificationService, never()).sendNotification(anyString());
	}

	@Test
	void shouldNotCreateTransactionDueToNotExistingRecipientId() throws Exception {
		TransactionCreateDTO transactionCreateDTO = new TransactionCreateDTO();
		transactionCreateDTO.setClientTransactionId(UUID.randomUUID());
		transactionCreateDTO.setAmount(BigDecimal.valueOf(100));
		transactionCreateDTO.setSender(UUID.fromString("5f8bae39-4d39-40cb-9c2f-f5aacd434d07"));
		transactionCreateDTO.setRecipient(UUID.fromString("a7b1c77c-b577-4381-aea8-394026d8eafc"));

		objectMapper.registerModule(new JavaTimeModule());
		String json = objectMapper.writeValueAsString(transactionCreateDTO);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v0/transaction" )
						.contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(json))
				.andExpect(status().isBadRequest())
				.andExpect(content().string("Account for recipient id not found: a7b1c77c-b577-4381-aea8-394026d8eafc"));

		verify(transactionNotificationService, never()).sendNotification(anyString());
	}

}
