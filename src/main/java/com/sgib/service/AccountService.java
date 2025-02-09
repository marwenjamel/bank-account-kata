package com.sgib.service;

import com.sgib.domain.dto.AccountDTO;
import com.sgib.domain.dto.AccountTransactionDTO;
import com.sgib.domain.mapper.AccountConverter;
import com.sgib.domain.mapper.AccountTransactionConverter;
import com.sgib.domain.model.Account;
import com.sgib.domain.model.AccountTransaction;
import com.sgib.exception.AccountNotFoundException;
import com.sgib.exception.InsufficientFundsException;
import com.sgib.exception.InvalidAmountException;
import com.sgib.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.sgib.domain.model.AccountTransactionType.DEPOSIT;
import static com.sgib.domain.model.AccountTransactionType.WITHDRAWAL;
import static com.sgib.exception.Messages.*;

@Service
public class AccountService {

    private AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountDTO createAccount() {
        Account account = Account.builder()
                .id(UUID.randomUUID())
                .balance(BigDecimal.ZERO)
                .accountTransactions(new ArrayList<>())
                .build();
        Account accountCreated = accountRepository.save(account);
        return AccountConverter.toDto(accountCreated);
    }

    public AccountTransactionDTO processTransaction(UUID accountId, AccountTransactionDTO accountTransactionDTO) {
        Account account = accountRepository.findOne(accountId)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCOUNT_NOT_FOUND, accountId)));
        AccountTransaction accountTransaction = AccountTransactionConverter.toEntity(accountTransactionDTO);
        validateTransaction(account, accountTransaction);
        if (DEPOSIT.equals(accountTransaction.getType()))
            account.setBalance(account.getBalance().add(accountTransaction.getAmount()));
        else
            account.setBalance(account.getBalance().subtract(accountTransaction.getAmount()));
        accountTransaction.setBalanceAfterTransaction(account.getBalance());
        account.getAccountTransactions().add(accountTransaction);
        return AccountTransactionConverter.toDto(accountTransaction);
    }

    public AccountDTO getAccount(UUID accountId) {
        Account account = accountRepository.findOne(accountId)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCOUNT_NOT_FOUND, accountId)));
        return AccountConverter.toDto(account);
    }

    public List<AccountTransactionDTO> getTransactionsByAccountId(UUID accountId) {
        Account account = accountRepository.findOne(accountId)
                .orElseThrow(() -> new AccountNotFoundException(String.format(ACCOUNT_NOT_FOUND, accountId)));
        return account.getAccountTransactions().stream().map(AccountTransactionConverter::toDto).toList();
    }

    private void validateTransaction(Account account, AccountTransaction accountTransaction) {
        if (accountTransaction.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException(AMOUNT_MUST_BE_GREATER_THAN_ZERO);
        }
        if (WITHDRAWAL.equals(accountTransaction.getType()) && account.getBalance().compareTo(accountTransaction.getAmount()) < 0)
            throw new InsufficientFundsException(INSUFFICIENT_FUNDS);
    }
}
