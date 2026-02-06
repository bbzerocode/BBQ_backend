package com.bbqpos.backend.service;

import com.bbqpos.backend.dto.order.OrderCreateDto;
import com.bbqpos.backend.dto.order.OrderDto;
import com.bbqpos.backend.dto.order.OrderItemCreateDto;
import com.bbqpos.backend.dto.order.PaymentDto;
import com.bbqpos.backend.dto.order.AddItemsDto;
import com.bbqpos.backend.model.*;
import com.bbqpos.backend.repository.*;
import com.bbqpos.backend.model.RestaurantTable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantTableRepository tableRepository;
    private final DishRepository dishRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final ReceivableRepository receivableRepository;
    private final EntityManager entityManager;

    @Autowired
    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository, RestaurantTableRepository tableRepository, DishRepository dishRepository, AccountRepository accountRepository, TransactionRepository transactionRepository, CustomerRepository customerRepository, ReceivableRepository receivableRepository, EntityManager entityManager) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.tableRepository = tableRepository;
        this.dishRepository = dishRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.receivableRepository = receivableRepository;
        this.entityManager = entityManager;
    }

    @Transactional
    public OrderDto createOrder(OrderCreateDto dto) {
        RestaurantTable table = tableRepository.findById(dto.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found"));

        if (table.getStatus() != RestaurantTable.Status.EMPTY) {
            throw new RuntimeException("Table is not available");
        }

        Order order = new Order();
        order.setTable(table);
        order.setOrderNo(generateOrderNo());
        order.setStatus(Order.Status.PENDING);
        order.setDiscount(BigDecimal.ZERO);

        List<OrderItem> items = dto.getItems().stream()
                .map(itemDto -> {
                    Dish dish = dishRepository.findById(itemDto.getDishId())
                            .orElseThrow(() -> new RuntimeException("Dish not found"));

                    if (dish.getStatus() != Dish.Status.AVAILABLE) {
                        throw new RuntimeException("Dish is not available: " + dish.getName());
                    }

                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setDish(dish);
                    orderItem.setQuantity(itemDto.getQuantity());
                    orderItem.setUnitPrice(dish.getPrice());
                    orderItem.setSubtotal(dish.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));

                    return orderItem;
                })
                .collect(Collectors.toList());

        BigDecimal totalAmount = items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(totalAmount);
        order.setFinalAmount(totalAmount);
        order.setItems(items);

        Order savedOrder = orderRepository.save(order);

        table.setStatus(RestaurantTable.Status.OCCUPIED);
        tableRepository.save(table);

        return new OrderDto(savedOrder);
    }

    @Transactional
    public OrderDto addItems(Long orderId, AddItemsDto dto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != Order.Status.PENDING) {
            throw new RuntimeException("Cannot add items to a non-pending order");
        }

        List<OrderItem> newItems = new ArrayList<>();
        List<OrderItem> existingItems = orderItemRepository.findByOrderId(orderId);
        BigDecimal addedAmount = BigDecimal.ZERO;

        for (OrderItemCreateDto itemDto : dto.getItems()) {
            Dish dish = dishRepository.findById(itemDto.getDishId())
                    .orElseThrow(() -> new RuntimeException("Dish not found"));

            if (dish.getStatus() != Dish.Status.AVAILABLE) {
                throw new RuntimeException("Dish is not available: " + dish.getName());
            }

            OrderItem existingItem = existingItems.stream()
                    .filter(item -> item.getDish().getId().equals(dish.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingItem == null) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setDish(dish);
                orderItem.setQuantity(itemDto.getQuantity());
                orderItem.setUnitPrice(dish.getPrice());
                orderItem.setSubtotal(dish.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity())));
                newItems.add(orderItem);
                addedAmount = addedAmount.add(orderItem.getSubtotal());
            } else {
                int oldQuantity = existingItem.getQuantity();
                int newQuantity = oldQuantity + itemDto.getQuantity();
                BigDecimal addedSubtotal = dish.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
                existingItem.setQuantity(newQuantity);
                existingItem.setSubtotal(dish.getPrice().multiply(BigDecimal.valueOf(newQuantity)));
                newItems.add(existingItem);
                addedAmount = addedAmount.add(addedSubtotal);
            }
        }

        orderItemRepository.saveAll(newItems);

        order.setTotalAmount(order.getTotalAmount().add(addedAmount));
        order.setFinalAmount(order.getFinalAmount().add(addedAmount));
        Order updatedOrder = orderRepository.save(order);

        return new OrderDto(updatedOrder);
    }

    @Transactional
    public OrderDto payOrder(Long id, PaymentDto paymentDto) {
        System.out.println("=== Starting payment for order ID: " + id + " ===");
        System.out.println("Payment method: " + paymentDto.getPaymentMethod());
        System.out.println("Discount: " + paymentDto.getDiscount());
        
        Order order = orderRepository.findOrderById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        System.out.println("Order found: " + order.getOrderNo() + ", Status: " + order.getStatus());

        if (order.getStatus() != Order.Status.PENDING) {
            throw new RuntimeException("Order is not pending payment");
        }

        BigDecimal discount = paymentDto.getDiscount() != null ? paymentDto.getDiscount() : BigDecimal.ZERO;
        BigDecimal finalAmount = order.getTotalAmount().subtract(discount);

        System.out.println("Total amount: " + order.getTotalAmount());
        System.out.println("Discount: " + discount);
        System.out.println("Final amount: " + finalAmount);

        order.setDiscount(discount);
        order.setFinalAmount(finalAmount);
        order.setPaymentMethod(paymentDto.getPaymentMethod());
        
        RestaurantTable table = order.getTable();
        System.out.println("Table: " + table.getName() + ", ID: " + table.getId());
        
        if ("挂账".equals(paymentDto.getPaymentMethod())) {
            System.out.println("Processing credit payment (挂账)");
            order.setStatus(Order.Status.UNPAID);
            
            final String customerName;
            String inputCustomerName = paymentDto.getCustomerName();
            if (inputCustomerName == null || inputCustomerName.trim().isEmpty()) {
                customerName = table.getName();
            } else {
                customerName = inputCustomerName;
            }
            
            System.out.println("Customer name: " + customerName);
            
            Customer customer = customerRepository.findByName(customerName)
                    .orElseGet(() -> {
                        Customer newCustomer = new Customer();
                        newCustomer.setName(customerName);
                        return customerRepository.save(newCustomer);
                    });
            
            System.out.println("Customer ID: " + customer.getId());
            
            Receivable receivable = new Receivable();
            receivable.setCustomer(customer);
            receivable.setAmount(finalAmount);
            receivable.setStatus(Receivable.Status.PENDING);
            receivableRepository.save(receivable);
            
            System.out.println("Receivable created");
        } else {
            System.out.println("Processing regular payment");
            order.setStatus(Order.Status.PAID);
            order.setPaidAt(LocalDateTime.now());

            Account.Type accountType = getAccountTypeFromPaymentMethod(paymentDto.getPaymentMethod());
            System.out.println("Account type: " + accountType);
            
            Account account = accountRepository.findByType(accountType)
                    .orElseThrow(() -> new RuntimeException("Account not found for payment method: " + paymentDto.getPaymentMethod()));

            System.out.println("Account found: " + account.getType() + ", Current balance: " + account.getBalance());

            account.setBalance(account.getBalance().add(finalAmount));
            accountRepository.save(account);

            System.out.println("Account updated, new balance: " + account.getBalance());

            Transaction transaction = new Transaction();
            transaction.setAccount(account);
            transaction.setType(Transaction.Type.INCOME);
            transaction.setAmount(finalAmount);
            transaction.setDescription("Order payment: " + order.getOrderNo());
            transactionRepository.save(transaction);
            
            System.out.println("Transaction created");
        }

        System.out.println("Saving order...");
        Order updatedOrder = orderRepository.save(order);
        System.out.println("Order saved");
        
        System.out.println("Updating table status...");
        table.setStatus(RestaurantTable.Status.EMPTY);
        tableRepository.save(table);
        System.out.println("Table status updated");
        
        System.out.println("Creating OrderDto...");
        OrderDto result = new OrderDto(updatedOrder);
        System.out.println("=== Payment completed successfully ===");
        
        return result;
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findByIdWithTable(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return new OrderDto(order);
    }

    public List<OrderDto> getOrdersByTableId(Long tableId) {
        return orderRepository.findByTableId(tableId).stream()
                .map(OrderDto::new)
                .collect(Collectors.toList());
    }

    private String generateOrderNo() {
        String prefix = "ORD-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
        long count = orderRepository.count();
        return prefix + String.format("%03d", count + 1);
    }

    private Account.Type getAccountTypeFromPaymentMethod(String paymentMethod) {
        System.out.println("=== getAccountTypeFromPaymentMethod called ===");
        System.out.println("Payment method: [" + paymentMethod + "]");
        System.out.println("Payment method length: " + (paymentMethod != null ? paymentMethod.length() : "null"));
        if (paymentMethod != null) {
            System.out.println("Payment method bytes: " + java.util.Arrays.toString(paymentMethod.getBytes()));
            System.out.println("Payment method lowercase: [" + paymentMethod.toLowerCase() + "]");
        }
        
        if (paymentMethod == null) {
            System.out.println("Returning CASH (null payment method)");
            return Account.Type.CASH;
        }
        
        Account.Type result;
        switch (paymentMethod.toLowerCase()) {
            case "cash":
            case "现金支付":
            case "现金":
                result = Account.Type.CASH;
                break;
            case "wechat":
            case "wechat pay":
            case "微信支付":
            case "微信":
                result = Account.Type.WECHAT;
                break;
            case "alipay":
            case "ali pay":
            case "支付宝":
                result = Account.Type.ALIPAY;
                break;
            case "bank":
            case "bank card":
            case "银行卡":
                result = Account.Type.BANK;
                break;
            default:
                result = Account.Type.CASH;
                break;
        }
        
        System.out.println("Returning account type: " + result);
        System.out.println("=== getAccountTypeFromPaymentMethod completed ===");
        return result;
    }

}
