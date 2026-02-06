package com.bbqpos.backend.dto.order;

import com.bbqpos.backend.model.Order;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrderDto {

    private Long id;
    private Long tableId;
    private String tableName;
    private String orderNo;
    private String status;
    private BigDecimal totalAmount;
    private BigDecimal discount;
    private BigDecimal finalAmount;
    private String paymentMethod;
    private List<OrderItemDto> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime paidAt;

    public OrderDto(Order order) {
        System.out.println("=== OrderDto constructor called ===");
        System.out.println("Order ID: " + order.getId());
        System.out.println("Order status: " + order.getStatus());
        
        this.id = order.getId();
        this.tableId = order.getTable() != null ? order.getTable().getId() : null;
        this.tableName = order.getTable() != null ? order.getTable().getName() : null;
        this.orderNo = order.getOrderNo();
        this.status = order.getStatus() != null ? order.getStatus().name() : null;
        this.totalAmount = order.getTotalAmount();
        this.discount = order.getDiscount();
        this.finalAmount = order.getFinalAmount();
        this.paymentMethod = order.getPaymentMethod();
        
        System.out.println("Processing items...");
        System.out.println("Items list: " + (order.getItems() != null ? order.getItems().size() + " items" : "null"));
        
        this.items = order.getItems() != null ? order.getItems().stream()
                .map(item -> {
                    System.out.println("Creating OrderItemDto for item ID: " + item.getId());
                    return new OrderItemDto(item);
                })
                .collect(Collectors.toList()) : null;
        
        System.out.println("Items processed");
        
        this.createdAt = order.getCreatedAt();
        this.updatedAt = order.getUpdatedAt();
        this.paidAt = order.getPaidAt();
        
        System.out.println("=== OrderDto constructor completed ===");
    }

}
