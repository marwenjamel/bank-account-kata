package com.sgib.domain.mapper;

import com.sgib.domain.dto.AccountDTO;
import com.sgib.domain.model.Account;

public class AccountConverter {

    public static AccountDTO toDto(Account account){
        return AccountDTO.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .build();
    }

    public static Account toEntity(AccountDTO accountDTO){
        return Account.builder()
                .id(accountDTO.getId())
                .balance(accountDTO.getBalance())
                .build();
    }
}
