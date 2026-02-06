package com.bbqpos.backend.dto.order;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class OrderItemCreateDto {

    @NotNull
    private Long dishId;

    @NotNull
    @Positive
    private int quantity;

    public Long getDishId() {
        return dishId;
    }

    public void setDishId(Long dishId) {
        this.dishId = dishId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
