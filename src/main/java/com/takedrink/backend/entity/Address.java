package com.takedrink.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
public class Address extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "recipient_name", nullable = false, length = 150)
  private String recipientName;

  @Column(name = "phone", nullable = false, length = 20)
  private String phone;

  @Column(name = "address_line", nullable = false, length = 255)
  private String addressLine;

  @Column(name = "ward", length = 100)
  private String ward;

  @Column(name = "district", length = 100)
  private String district;

  @Column(name = "province", length = 100)
  private String province;

  @Column(name = "is_default", nullable = false)
  private Boolean defaultAddress = false;
}