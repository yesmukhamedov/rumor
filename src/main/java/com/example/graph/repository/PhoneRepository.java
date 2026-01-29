package com.example.graph.repository;

import com.example.graph.model.PhoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneRepository extends JpaRepository<PhoneEntity, Long> {
    boolean existsByValue(String value);

    boolean existsByNodeId(Long nodeId);
}
