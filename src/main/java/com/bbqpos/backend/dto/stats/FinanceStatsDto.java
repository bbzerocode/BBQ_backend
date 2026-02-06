package com.bbqpos.backend.dto.stats;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FinanceStatsDto {

    private BigDecimal cashBalance;
    private BigDecimal wechatBalance;
    private BigDecimal alipayBalance;
    private BigDecimal bankBalance;
    private BigDecimal totalBalance;

    public FinanceStatsDto(BigDecimal cashBalance, BigDecimal wechatBalance, BigDecimal alipayBalance, BigDecimal bankBalance) {
        this.cashBalance = cashBalance;
        this.wechatBalance = wechatBalance;
        this.alipayBalance = alipayBalance;
        this.bankBalance = bankBalance;
        this.totalBalance = cashBalance.add(wechatBalance).add(alipayBalance).add(bankBalance);
    }

}
