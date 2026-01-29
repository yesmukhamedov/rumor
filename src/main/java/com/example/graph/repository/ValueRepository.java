package com.example.graph.repository;

import com.example.graph.model.ValueEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValueRepository extends JpaRepository<ValueEntity, Long> {
    Optional<ValueEntity> findByText(String text);
}
