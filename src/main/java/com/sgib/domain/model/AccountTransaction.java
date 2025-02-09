package com.sgib.domain.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AccountTransaction {
    private LocalDateTime date;
    private BigDecimal amount;
    private BigDecimal balanceAfterTransaction;
    private AccountTransactionType type;
}