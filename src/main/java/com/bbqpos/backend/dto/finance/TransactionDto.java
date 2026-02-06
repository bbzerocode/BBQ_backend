package com.bbqpos.backend.dto.finance;

import com.bbqpos.backend.model.Transaction;
import com.bbqpos.backend.model.Receivable;
import com.bbqpos.backend.model.Payable;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDto {

    private Long id;
    private Long accountId;
    private String accountType;
    private String type;
    private BigDecimal amount;
    private String category;
    private String description;
    private LocalDateTime createdAt;
    private String status;

    public TransactionDto(Transaction transaction) {
        this.id = transaction.getId();
        this.accountId = transaction.getAccount().getId();
        this.accountType = transaction.getAccount().getType().name();
        this.type = transaction.getType().name();
        this.amount = transaction.getAmount();
        this.category = transaction.getCategory();
        this.description = transaction.getDescription();
        this.createdAt = transaction.getCreatedAt();
        this.status = "COMPLETED";
    }

    public TransactionDto(Receivable receivable) {
        this.id = receivable.getId();
        this.type = "INCOME";
        this.amount = receivable.getAmount();
        this.category = receivable.getCategory();
        this.description = receivable.getDescription();
        this.createdAt = receivable.getCreatedAt();
        this.status = receivable.getStatus().name();
    }

    public TransactionDto(Payable payable) {
        this.id = payable.getId();
        this.type = "EXPENSE";
        this.amount = payable.getAmount();
        this.category = payable.getCategory();
        this.description = payable.getDescription();
        this.createdAt = payable.getCreatedAt();
        this.status = payable.getStatus().name();
    }

}
