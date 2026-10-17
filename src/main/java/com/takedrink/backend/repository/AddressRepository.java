package com.takedrink.backend.repository;

import com.takedrink.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

  List<Address> findByUserId(Long userId);

  Optional<Address> findByIdAndUserId(Long id, Long userId);

  Optional<Address> findByUserIdAndDefaultAddressTrue(Long userId);
}
