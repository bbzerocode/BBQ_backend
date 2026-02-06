package com.bbqpos.backend.dto.finance;

import com.bbqpos.backend.model.Payable;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PayableDto {

    private Long id;
    private Long supplierId;
    private String supplierName;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private BigDecimal remainingAmount;
    private String status;
    private String category;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;

    public PayableDto(Payable payable) {
        this.id = payable.getId();
        this.supplierId = payable.getSupplier() != null ? payable.getSupplier().getId() : null;
        this.supplierName = payable.getSupplier() != null ? payable.getSupplier().getName() : "未知供应商";
        this.amount = payable.getAmount();
        this.paidAmount = payable.getPaidAmount();
        this.remainingAmount = payable.getAmount().subtract(payable.getPaidAmount());
        this.status = payable.getStatus().name();
        this.category = payable.getCategory();
        this.description = payable.getDescription();
        this.createdAt = payable.getCreatedAt();
        this.paidAt = payable.getPaidAt();
    }

}
