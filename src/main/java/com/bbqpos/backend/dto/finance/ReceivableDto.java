package com.bbqpos.backend.dto.finance;

import com.bbqpos.backend.model.Receivable;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ReceivableDto {

    private Long id;
    private Long customerId;
    private String customerName;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private String status;
    private String category;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public ReceivableDto(Receivable receivable) {
        this.id = receivable.getId();
        this.customerId = receivable.getCustomer() != null ? receivable.getCustomer().getId() : null;
        this.customerName = receivable.getCustomer() != null ? receivable.getCustomer().getName() : "未知客户";
        this.amount = receivable.getAmount();
        this.paidAmount = receivable.getPaidAmount();
        this.remainingAmount = receivable.getAmount().subtract(receivable.getPaidAmount());
        this.status = receivable.getStatus().name();
        this.category = receivable.getCategory();
        this.description = receivable.getDescription();
        this.createdAt = receivable.getCreatedAt();
        this.paidAt = receivable.getPaidAt();
    }

}
