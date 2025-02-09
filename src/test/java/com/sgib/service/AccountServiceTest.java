package com.sgib.service;

import com.sgib.domain.dto.AccountTransactionDTO;
import com.sgib.domain.model.Account;
import com.sgib.domain.model.AccountTransaction;
import com.sgib.domain.model.AccountTransactionType;
import com.sgib.exception.AccountNotFoundException;
import com.sgib.exception.InsufficientFundsException;
import com.sgib.exception.InvalidAmountException;
import com.sgib.repository.AccountRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sgib.exception.Messages.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    public void should_deposit_amount_when_deposit_transaction_and_transaction_is_valid() {
        // Given
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder().id(accountId).balance(BigDecimal.ZERO)
                .accountTransactions(new ArrayList<>()).build();
        when(accountRepository.findOne(accountId)).thenReturn(Optional.of(account));
        AccountTransactionDTO accountTransactionDTO = AccountTransactionDTO.builder()
                .amount(BigDecimal.valueOf(10))
                .type("DEPOSIT").build();
        // When
        AccountTransactionDTO accountTransaction = accountService.processTransaction(accountId, accountTransactionDTO);
        // Then
        assertEquals(BigDecimal.valueOf(10), accountTransaction.getBalanceAfterTransaction());
    }

    @Test
    public void should_withdraw_amount_when_transaction_is_withdrawal_and_transaction_is_valid() {
        // Given
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder().id(accountId).balance(BigDecimal.valueOf(100))
                .accountTransactions(new ArrayList<>()).build();
        when(accountRepository.findOne(accountId)).thenReturn(Optional.of(account));
        AccountTransactionDTO accountTransactionDTO = AccountTransactionDTO.builder()
                .amount(BigDecimal.valueOf(20))
                .type("WITHDRAWAL").build();
        // When
        AccountTransactionDTO accountTransaction = accountService.processTransaction(accountId, accountTransactionDTO);
        // Then
        assertEquals(BigDecimal.valueOf(80), accountTransaction.getBalanceAfterTransaction());
    }

    @Test
    public void should_return_list_of_transaction_for_the_account_when_the_account_exists() {
        // Given
        UUID accountId = UUID.randomUUID();
        List<AccountTransaction> accountTransactionList = new ArrayList<>();
        accountTransactionList.add(new AccountTransaction(LocalDateTime.now(), BigDecimal.valueOf(50), BigDecimal.valueOf(50), AccountTransactionType.DEPOSIT));
        accountTransactionList.add(new AccountTransaction(LocalDateTime.now(), BigDecimal.valueOf(30), BigDecimal.valueOf(20), AccountTransactionType.WITHDRAWAL));
        Account account = Account.builder().id(accountId).balance(BigDecimal.valueOf(100))
                .accountTransactions(accountTransactionList).build();
        when(accountRepository.findOne(accountId)).thenReturn(Optional.of(account));
        // When
        List<AccountTransactionDTO> accountTransactionDtoList = accountService.getTransactionsByAccountId(accountId);
        // Then
        assertEquals(2, accountTransactionDtoList.size());
        assertEquals(BigDecimal.valueOf(50), accountTransactionDtoList.get(0).getAmount());
        assertEquals(BigDecimal.valueOf(30), accountTransactionDtoList.get(1).getAmount());
        assertEquals("DEPOSIT", accountTransactionDtoList.get(0).getType());
        assertEquals("WITHDRAWAL", accountTransactionDtoList.get(1).getType());
        assertEquals(BigDecimal.valueOf(50), accountTransactionDtoList.get(0).getBalanceAfterTransaction());
        assertEquals(BigDecimal.valueOf(20), accountTransactionDtoList.get(1).getBalanceAfterTransaction());
    }

    @Test
    public void should_throw_error_when_withdrawal_is_greater_than_balance() {
        // Given
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder().id(accountId).balance(BigDecimal.valueOf(10))
                .accountTransactions(new ArrayList<>()).build();
        when(accountRepository.findOne(accountId)).thenReturn(Optional.of(account));
        AccountTransactionDTO accountTransactionDTO = AccountTransactionDTO.builder()
                .amount(BigDecimal.valueOf(20))
                .type("WITHDRAWAL").build();
        // When
        Exception exception = Assertions.assertThrows(InsufficientFundsException.class, () ->
                accountService.processTransaction(accountId, accountTransactionDTO));
        assertEquals(INSUFFICIENT_FUNDS, exception.getMessage());
    }

    @Test
    public void should_throw_error_when_account_not_found() {
        // Given
        UUID accountId = UUID.randomUUID();
        when(accountRepository.findOne(accountId)).thenReturn(Optional.empty());
        AccountTransactionDTO accountTransactionDTO = AccountTransactionDTO.builder()
                .amount(BigDecimal.valueOf(20))
                .type("WITHDRAWAL").build();
        // When
        Exception exception = Assertions.assertThrows(AccountNotFoundException.class, () ->
                accountService.processTransaction(accountId, accountTransactionDTO));
        assertEquals(String.format(ACCOUNT_NOT_FOUND, accountId), exception.getMessage());
    }

    @Test
    public void should_throw_error_when_deposit_amount_is_less_or_equal_than_zero() {
        // Given
        UUID accountId = UUID.randomUUID();
        Account account = Account.builder().id(accountId).balance(BigDecimal.ZERO)
                .accountTransactions(new ArrayList<>()).build();
        when(accountRepository.findOne(accountId)).thenReturn(Optional.of(account));
        AccountTransactionDTO accountTransactionDTO = AccountTransactionDTO.builder()
                .amount(BigDecimal.valueOf(-10))
                .type("DEPOSIT").build();
        // When
        Exception exception = Assertions.assertThrows(InvalidAmountException.class, () ->
                accountService.processTransaction(accountId, accountTransactionDTO));
        assertEquals(AMOUNT_MUST_BE_GREATER_THAN_ZERO, exception.getMessage());
    }
}