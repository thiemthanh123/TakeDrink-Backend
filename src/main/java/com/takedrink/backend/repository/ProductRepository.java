
package com.takedrink.backend.repository;

import com.takedrink.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

  List<Product> findByNameContainingIgnoreCase(String keyword);

  List<Product> findByPriceGreaterThan(BigDecimal price);

  List<Product> findByAvailableTrueOrderByPriceAsc();
}
