package com.sgib.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AccountTransactionDTO {

    private LocalDateTime transactionDate;
    private BigDecimal amount;
    private BigDecimal balanceAfterTransaction;
    private String type;

}
