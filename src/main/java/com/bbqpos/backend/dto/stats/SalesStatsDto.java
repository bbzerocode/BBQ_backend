package com.bbqpos.backend.dto.stats;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalesStatsDto {

    private BigDecimal totalSales;
    private int totalOrders;
    private BigDecimal averageOrderAmount;

    public SalesStatsDto(BigDecimal totalSales, int totalOrders, BigDecimal averageOrderAmount) {
        this.totalSales = totalSales;
        this.totalOrders = totalOrders;
        this.averageOrderAmount = averageOrderAmount;
    }

}
