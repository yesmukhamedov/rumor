package com.example.graph.repository;

import com.example.graph.model.NodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeRepository extends JpaRepository<NodeEntity, Long> {
}
