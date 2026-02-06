package com.bbqpos.backend.controller;

import com.bbqpos.backend.dto.order.OrderCreateDto;
import com.bbqpos.backend.dto.order.OrderDto;
import com.bbqpos.backend.dto.order.PaymentDto;
import com.bbqpos.backend.dto.order.AddItemsDto;
import com.bbqpos.backend.service.OrderService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable Long id) {
        OrderDto order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderCreateDto dto) {
        OrderDto order = orderService.createOrder(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<OrderDto> payOrder(@PathVariable Long id, @RequestBody PaymentDto paymentDto) {
        OrderDto order = orderService.payOrder(id, paymentDto);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<OrderDto> addItems(@PathVariable Long id, @Valid @RequestBody AddItemsDto dto) {
        OrderDto order = orderService.addItems(id, dto);
        return ResponseEntity.ok(order);
    }

    @GetMapping("/table/{tableId}")
    public ResponseEntity<List<OrderDto>> getOrdersByTableId(@PathVariable Long tableId) {
        List<OrderDto> orders = orderService.getOrdersByTableId(tableId);
        return ResponseEntity.ok(orders);
    }

}
