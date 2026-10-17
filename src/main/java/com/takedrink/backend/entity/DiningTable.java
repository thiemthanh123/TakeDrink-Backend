package com.takedrink.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "dining_tables")
@Getter
@Setter
public class DiningTable extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "table_number", nullable = false, unique = true, length = 30)
  private String tableNumber;

  @Column(name = "qr_token", nullable = false, unique = true, length = 100)
  private String qrToken;

  @Column(name = "capacity", nullable = false)
  private Integer capacity = 2;

  @Column(name = "is_active", nullable = false)
  private Boolean active = true;
}
