package com.takedrink.backend.repository;

import com.takedrink.backend.entity.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiningTableRepository
        extends JpaRepository<DiningTable, Long> {

  boolean existsByIdAndActiveTrue(Long id);
}
