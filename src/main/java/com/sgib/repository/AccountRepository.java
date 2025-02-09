package com.sgib.repository;

import com.sgib.domain.model.Account;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountRepository {

    private final ConcurrentHashMap<UUID, Account> accounts = new ConcurrentHashMap<>();

    public Account save(Account account){
        accounts.put(account.getId(),account);
        return account;
    }

    public Optional<Account> findOne(UUID id){
        return Optional.ofNullable(accounts.get(id));
    }


}
