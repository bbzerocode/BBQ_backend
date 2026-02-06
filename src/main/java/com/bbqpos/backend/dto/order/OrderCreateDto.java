package com.bbqpos.backend.dto.order;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.util.List;

public class OrderCreateDto {

    @NotNull
    private Long tableId;

    @NotNull
    @Size(min = 1)
    private List<OrderItemCreateDto> items;

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public List<OrderItemCreateDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemCreateDto> items) {
        this.items = items;
    }

}
