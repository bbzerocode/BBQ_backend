package com.bbqpos.backend.dto.order;

import javax.validation.constraints.NotNull;
import java.util.List;

public class AddItemsDto {

    @NotNull
    private List<OrderItemCreateDto> items;

    public List<OrderItemCreateDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemCreateDto> items) {
        this.items = items;
    }
}
