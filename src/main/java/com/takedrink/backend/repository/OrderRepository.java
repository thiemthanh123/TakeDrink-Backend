package com.takedrink.backend.repository;

import com.takedrink.backend.entity.Order;
import com.takedrink.backend.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

  List<Order> findAllByOrderByCreatedAtDesc();

  List<Order> findByUser_IdOrderByCreatedAtDesc(Long userId);

  Optional<Order> findByOrderCode(String orderCode);

  boolean existsByOrderCode(String orderCode);

  List<Order> findByStatus(OrderStatus status);
}
