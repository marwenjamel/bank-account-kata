package com.sgib.domain.mapper;

import com.sgib.domain.dto.AccountTransactionDTO;
import com.sgib.domain.model.AccountTransaction;
import com.sgib.domain.model.AccountTransactionType;

import java.util.Optional;

public class AccountTransactionConverter {

    public static AccountTransactionDTO toDto(AccountTransaction accountTransaction) {
        if (accountTransaction == null)
            return null;
        return AccountTransactionDTO.builder()
                .transactionDate(accountTransaction.getDate())
                .type(Optional.ofNullable(accountTransaction.getType()).map(Enum::name).orElse(null))
                .amount(accountTransaction.getAmount())
                .balanceAfterTransaction(accountTransaction.getBalanceAfterTransaction())
                .build();
    }

    public static AccountTransaction toEntity(AccountTransactionDTO accountTransactionDTO) {
        if (accountTransactionDTO == null)
            return null;
        return AccountTransaction.builder()
                .date(accountTransactionDTO.getTransactionDate())
                .type(AccountTransactionType.valueOf(accountTransactionDTO.getType()))
                .amount(accountTransactionDTO.getAmount())
                .balanceAfterTransaction(accountTransactionDTO.getBalanceAfterTransaction())
                .build();
    }
}
