package com.takedrink.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "order_code", nullable = false, unique = true, length = 50)
  private String orderCode;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "order_type", nullable = false, length = 20)
  private String orderType;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dining_table_id")
  private DiningTable diningTable;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "address_id")
  private Address address;

  @Column(name = "recipient_name", length = 150)
  private String recipientName;

  @Column(name = "recipient_phone", length = 20)
  private String recipientPhone;

  @Column(name = "delivery_address", length = 500)
  private String deliveryAddress;

  @Column(name = "status", nullable = false, length = 30)
  private String status = "PENDING";

  @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
  private BigDecimal totalAmount = BigDecimal.ZERO;

  @Column(name = "note", length = 1000)
  private String note;

  @OneToMany(mappedBy = "order")
  private List<OrderItem> items = new ArrayList<>();

  @OneToMany(mappedBy = "order")
  private List<OrderStatusHistory> statusHistories = new ArrayList<>();

  @OneToMany(mappedBy = "order")
  private List<Payment> payments = new ArrayList<>();
}