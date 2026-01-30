package com.example.graph.service;

import com.example.graph.model.NodeValueEntity;
import com.example.graph.repository.NodeValueRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NodeValueService {
    private final NodeValueRepository nodeValueRepository;

    public NodeValueService(NodeValueRepository nodeValueRepository) {
        this.nodeValueRepository = nodeValueRepository;
    }

    public NodeValueEntity createCurrentValue(NodeValueEntity nodeValueEntity) {
        return nodeValueRepository.save(nodeValueEntity);
    }

    @Transactional(readOnly = true)
    public Optional<NodeValueEntity> getCurrentValue(Long nodeId, OffsetDateTime now) {
        return nodeValueRepository.findCurrentValueByNodeId(nodeId, now);
    }

    @Transactional(readOnly = true)
    public String getCurrentValueText(Long nodeId, OffsetDateTime now) {
        return getCurrentValue(nodeId, now).map(NodeValueEntity::getValue).orElse(null);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getCurrentValues(OffsetDateTime now) {
        List<NodeValueEntity> values = nodeValueRepository.findCurrentValues(now);
        return values.stream()
            .collect(Collectors.toMap(value -> value.getNode().getId(), NodeValueEntity::getValue, (a, b) -> a));
    }

}
