package com.sgib.controller;

import com.sgib.domain.dto.AccountDTO;
import com.sgib.domain.dto.AccountTransactionDTO;
import com.sgib.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createAccount() {
        AccountDTO accountCreated = accountService.createAccount();
        return new ResponseEntity<>(accountCreated, HttpStatus.CREATED);
    }

    @GetMapping(value = "/{accountId}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable String accountId) {
        AccountDTO account=accountService.getAccount(UUID.fromString(accountId));
        return new ResponseEntity<>(account, HttpStatus.OK);
    }

    @PostMapping("/{accountId}/transactions")
    public ResponseEntity<AccountTransactionDTO> processTransaction(@PathVariable String accountId, @RequestBody AccountTransactionDTO accountTransactionDTO) {
        AccountTransactionDTO accountTransactionDtoProcessed = accountService.processTransaction(UUID.fromString(accountId), accountTransactionDTO);
        return new ResponseEntity<>(accountTransactionDtoProcessed, HttpStatus.CREATED);
    }

    @GetMapping("/{accountId}/transactions")
    public ResponseEntity<List<AccountTransactionDTO>> getTransactions(@PathVariable String accountId) {
        List<AccountTransactionDTO> transactionsByAccountId = accountService.getTransactionsByAccountId(UUID.fromString(accountId));
        return new ResponseEntity<>(transactionsByAccountId,HttpStatus.OK);
    }

}
