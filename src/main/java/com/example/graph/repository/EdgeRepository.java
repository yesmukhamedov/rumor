package com.example.graph.repository;

import com.example.graph.model.EdgeEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EdgeRepository extends JpaRepository<EdgeEntity, Long> {
    boolean existsByFromNodeIdAndToNodeId(Long fromId, Long toId);

    List<EdgeEntity> findAllByFromNodeId(Long fromId);
}
