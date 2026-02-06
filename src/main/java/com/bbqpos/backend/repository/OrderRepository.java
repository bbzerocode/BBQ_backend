package com.bbqpos.backend.repository;

import com.bbqpos.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(Order.Status status);

    List<Order> findByTableId(Long tableId);

    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.table LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.dish WHERE o.id = :id")
    Optional<Order> findByIdWithTable(@Param("id") Long id);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.table LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.dish WHERE o.id = :id")
    Optional<Order> findOrderById(@Param("id") Long id);

}
