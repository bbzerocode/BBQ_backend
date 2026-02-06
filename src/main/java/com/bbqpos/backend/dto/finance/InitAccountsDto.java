package com.bbqpos.backend.dto.finance;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InitAccountsDto {
    private BigDecimal cashAmount;
    private BigDecimal wechatAmount;
    private BigDecimal alipayAmount;
    private BigDecimal bankAmount;

    public InitAccountsDto() {
        // 默认构造函数
    }

    public InitAccountsDto(BigDecimal cashAmount, BigDecimal wechatAmount, BigDecimal alipayAmount, BigDecimal bankAmount) {
        this.cashAmount = cashAmount;
        this.wechatAmount = wechatAmount;
        this.alipayAmount = alipayAmount;
        this.bankAmount = bankAmount;
    }
}