package com.bbqpos.backend.dto.finance;

import com.bbqpos.backend.model.Account;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountDto {

    private Long id;
    private String type;
    private BigDecimal balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AccountDto(Account account) {
        this.id = account.getId();
        this.type = account.getType().name();
        this.balance = account.getBalance();
        this.createdAt = account.getCreatedAt();
        this.updatedAt = account.getUpdatedAt();
    }

}
