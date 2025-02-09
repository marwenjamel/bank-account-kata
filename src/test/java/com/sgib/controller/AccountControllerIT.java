package com.sgib.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sgib.domain.dto.AccountDTO;
import com.sgib.domain.dto.AccountTransactionDTO;
import com.sgib.exception.Messages;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.sgib.exception.Messages.ACCOUNT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AccountControllerIT {

    public static final String DEPOSIT = "DEPOSIT";
    public static final String WITHDRAWAL = "WITHDRAWAL";
    public static String API_URL = "/api/v1/accounts";
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void before(){
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void should_test_full_account_lifecycle() throws Exception {
        // Create Account
        MockHttpServletResponse response = mockMvc.perform(post(API_URL))
                .andExpect(status().isCreated())
                .andReturn().getResponse();

        AccountDTO accountDTO = objectMapper.readValue(response.getContentAsString(), AccountDTO.class);

        assertNotNull(accountDTO);
        assertNotNull(accountDTO.getId());
        String accountId = String.valueOf(accountDTO.getId());

        // Deposit 100.00
        AccountTransactionDTO depositRequest1 = AccountTransactionDTO.builder()
                .transactionDate(LocalDateTime.now())
                .amount(BigDecimal.valueOf(100))
                .type(DEPOSIT).build();

        mockMvc.perform(post(API_URL + "/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest1)))
                .andExpect(status().isCreated());

       // Withdraw 30.00
        AccountTransactionDTO withdrawalRequest1 = AccountTransactionDTO.builder()
                .transactionDate(LocalDateTime.now())
                .amount(BigDecimal.valueOf(30))
                .type(WITHDRAWAL).build();

        mockMvc.perform(post(API_URL + "/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest1)))
                .andExpect(status().isCreated());

        // Deposit another 50.00
        AccountTransactionDTO depositRequest2 = AccountTransactionDTO.builder()
                .transactionDate(LocalDateTime.now())
                .amount(BigDecimal.valueOf(50))
                .type(DEPOSIT).build();

        mockMvc.perform(post(API_URL + "/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest2)))
                .andExpect(status().isCreated());

        // Withdraw 20.00
        AccountTransactionDTO withdrawalRequest2 = AccountTransactionDTO.builder()
                .transactionDate(LocalDateTime.now())
                .amount(BigDecimal.valueOf(20))
                .type(WITHDRAWAL).build();

        mockMvc.perform(post(API_URL + "/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest2)))
                .andExpect(status().isCreated());

        // Withdraw 10.00
        AccountTransactionDTO withdrawalRequest3 = AccountTransactionDTO.builder()
                .transactionDate(LocalDateTime.now())
                .amount(BigDecimal.valueOf(10))
                .type(WITHDRAWAL).build();

        mockMvc.perform(post(API_URL + "/{accountId}/transactions", accountId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest3)))
                .andExpect(status().isCreated());

        // getAccount
        MockHttpServletResponse AccountResponse = mockMvc.perform(get(API_URL + "/{accountId}", accountId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();


        AccountDTO account = objectMapper.readValue(AccountResponse.getContentAsString(), AccountDTO.class);

        assertNotNull(account);
        assertNotNull(account.getId());
        assertEquals(BigDecimal.valueOf(90),account.getBalance());

        //get all transactions
        MockHttpServletResponse accountTransactionResponse =
                mockMvc.perform(get(API_URL+"/{accountId}/transactions", accountId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<AccountTransactionDTO> responseTransactions = objectMapper.readValue(
                accountTransactionResponse.getContentAsString(),
                new TypeReference<>() {
                }
        );
        assertEquals(BigDecimal.valueOf(100), responseTransactions.get(0).getAmount());
        assertEquals(BigDecimal.valueOf(100), responseTransactions.get(0).getBalanceAfterTransaction());
        assertEquals(DEPOSIT, responseTransactions.get(0).getType());
        
        assertEquals(BigDecimal.valueOf(30), responseTransactions.get(1).getAmount());
        assertEquals(BigDecimal.valueOf(70), responseTransactions.get(1).getBalanceAfterTransaction());
        assertEquals(WITHDRAWAL, responseTransactions.get(1).getType());
        
        assertEquals(BigDecimal.valueOf(50), responseTransactions.get(2).getAmount());
        assertEquals(BigDecimal.valueOf(120), responseTransactions.get(2).getBalanceAfterTransaction());
        assertEquals(DEPOSIT, responseTransactions.get(2).getType());

        assertEquals(BigDecimal.valueOf(20), responseTransactions.get(3).getAmount());
        assertEquals(BigDecimal.valueOf(100), responseTransactions.get(3).getBalanceAfterTransaction());
        assertEquals(WITHDRAWAL, responseTransactions.get(3).getType());

        assertEquals(BigDecimal.valueOf(10), responseTransactions.get(4).getAmount());
        assertEquals(BigDecimal.valueOf(90), responseTransactions.get(4).getBalanceAfterTransaction());
        assertEquals(WITHDRAWAL, responseTransactions.get(4).getType());

        
    }

    @Test
    public void should_throw_error_when_account_not_found() throws Exception {
        String nonExistingAccountId=String.valueOf(UUID.randomUUID());
        mockMvc.perform(get(API_URL+"/{accountId}", nonExistingAccountId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format(ACCOUNT_NOT_FOUND,nonExistingAccountId)));
    }

    @Test
    public void should_throw_insufficient_funds_error_when_withdrawal_is_greater_than_balance() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post(API_URL))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
        AccountDTO accountDTO = objectMapper.readValue(response.getContentAsString(), AccountDTO.class);
        AccountTransactionDTO withdrawalRequest = AccountTransactionDTO.builder()
                .transactionDate(LocalDateTime.now())
                .amount(BigDecimal.valueOf(10))
                .type(WITHDRAWAL).build();

        mockMvc.perform(post(API_URL + "/{accountId}/transactions", accountDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawalRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Messages.INSUFFICIENT_FUNDS));
    }

    @Test
    public void should_throw_amount_not_valid_when_transaction_with_amount_equals_to_zero() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post(API_URL))
                .andExpect(status().isCreated())
                .andReturn().getResponse();
        AccountDTO accountDTO = objectMapper.readValue(response.getContentAsString(), AccountDTO.class);
        AccountTransactionDTO depositRequest = AccountTransactionDTO.builder()
                .transactionDate(LocalDateTime.now())
                .amount(BigDecimal.valueOf(0))
                .type(DEPOSIT).build();

        mockMvc.perform(post(API_URL + "/{accountId}/transactions", accountDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Messages.AMOUNT_MUST_BE_GREATER_THAN_ZERO));
    }

}