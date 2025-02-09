package com.sgib.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Account {

    private UUID id;
    private BigDecimal balance;
    private List<AccountTransaction> accountTransactions;

}
