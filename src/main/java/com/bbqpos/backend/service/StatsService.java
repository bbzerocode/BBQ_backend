package com.bbqpos.backend.service;

import com.bbqpos.backend.dto.stats.DishSalesStatsDto;
import com.bbqpos.backend.dto.stats.FinanceStatsDto;
import com.bbqpos.backend.dto.stats.SalesStatsDto;
import com.bbqpos.backend.model.Account;
import com.bbqpos.backend.model.Order;
import com.bbqpos.backend.model.OrderItem;
import com.bbqpos.backend.repository.AccountRepository;
import com.bbqpos.backend.repository.OrderItemRepository;
import com.bbqpos.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StatsService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;

    @Autowired
    public StatsService(OrderItemRepository orderItemRepository, OrderRepository orderRepository, AccountRepository accountRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
    }

    public List<DishSalesStatsDto> getDishSalesStats(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<OrderItem> orderItems = orderItemRepository.findByOrderCreatedAtBetween(startDate, LocalDateTime.now());

        Map<String, Integer> dishQuantityMap = new HashMap<>();
        Map<String, BigDecimal> dishAmountMap = new HashMap<>();

        for (OrderItem item : orderItems) {
            String dishName = item.getDish().getName();
            dishQuantityMap.put(dishName, dishQuantityMap.getOrDefault(dishName, 0) + item.getQuantity());
            dishAmountMap.put(dishName, dishAmountMap.getOrDefault(dishName, BigDecimal.ZERO).add(item.getSubtotal()));
        }

        int totalQuantity = dishQuantityMap.values().stream().mapToInt(Integer::intValue).sum();
        BigDecimal totalAmount = dishAmountMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);

        List<DishSalesStatsDto> stats = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : dishQuantityMap.entrySet()) {
            String dishName = entry.getKey();
            int quantity = entry.getValue();
            BigDecimal amount = dishAmountMap.get(dishName);
            double percentage = totalQuantity > 0 ? (double) quantity / totalQuantity * 100 : 0;
            stats.add(new DishSalesStatsDto(dishName, quantity, amount, percentage));
        }

        stats.sort(Comparator.comparingInt(DishSalesStatsDto::getQuantity).reversed());
        return stats;
    }

    public SalesStatsDto getSalesStats(int days) {
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        List<Order> orders = orderRepository.findByCreatedAtBetween(startDate, LocalDateTime.now());

        BigDecimal totalSales = orders.stream()
                .filter(order -> order.getStatus() == Order.Status.PAID)
                .map(Order::getFinalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalOrders = orders.size();
        BigDecimal averageOrderAmount = totalOrders > 0 ? totalSales.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO;

        return new SalesStatsDto(totalSales, totalOrders, averageOrderAmount);
    }

    public FinanceStatsDto getFinanceStats() {
        List<Account> accounts = accountRepository.findAll();
        Map<Account.Type, BigDecimal> balanceMap = new HashMap<>();

        for (Account account : accounts) {
            balanceMap.put(account.getType(), account.getBalance());
        }

        BigDecimal cashBalance = balanceMap.getOrDefault(Account.Type.CASH, BigDecimal.ZERO);
        BigDecimal wechatBalance = balanceMap.getOrDefault(Account.Type.WECHAT, BigDecimal.ZERO);
        BigDecimal alipayBalance = balanceMap.getOrDefault(Account.Type.ALIPAY, BigDecimal.ZERO);
        BigDecimal bankBalance = balanceMap.getOrDefault(Account.Type.BANK, BigDecimal.ZERO);

        return new FinanceStatsDto(cashBalance, wechatBalance, alipayBalance, bankBalance);
    }

}
