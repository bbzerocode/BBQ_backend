package com.bbqpos.backend.dto.order;

import com.bbqpos.backend.model.OrderItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDto {

    private Long id;
    private Long dishId;
    private String dishName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public OrderItemDto(OrderItem item) {
        System.out.println("=== OrderItemDto constructor called ===");
        System.out.println("OrderItem ID: " + item.getId());
        
        this.id = item.getId();
        this.dishId = item.getDish() != null ? item.getDish().getId() : null;
        this.dishName = item.getDish() != null ? item.getDish().getName() : null;
        this.quantity = item.getQuantity();
        this.unitPrice = item.getUnitPrice();
        this.subtotal = item.getSubtotal();
        
        System.out.println("=== OrderItemDto constructor completed ===");
    }

}
