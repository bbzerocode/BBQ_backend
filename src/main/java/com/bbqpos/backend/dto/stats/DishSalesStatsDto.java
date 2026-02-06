package com.bbqpos.backend.dto.stats;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DishSalesStatsDto {

    private String dishName;
    private int quantity;
    private BigDecimal amount;
    private double percentage;

    public DishSalesStatsDto(String dishName, int quantity, BigDecimal amount, double percentage) {
        this.dishName = dishName;
        this.quantity = quantity;
        this.amount = amount;
        this.percentage = percentage;
    }

}
