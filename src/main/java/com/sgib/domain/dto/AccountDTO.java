package com.sgib.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class AccountDTO {
    private UUID id;
    private BigDecimal balance;

}
