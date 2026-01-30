package com.example.graph.service.value;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.value.EdgeValueEntity;
import com.example.graph.repository.EdgeValueRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EdgeValueService {
    private final EdgeValueRepository edgeValueRepository;

    public EdgeValueService(EdgeValueRepository edgeValueRepository) {
        this.edgeValueRepository = edgeValueRepository;
    }

    public EdgeValueEntity createCurrentValue(EdgeEntity edge, String value, OffsetDateTime createdAt) {
        EdgeValueEntity edgeValue = new EdgeValueEntity();
        edgeValue.setEdge(edge);
        edgeValue.setValue(value);
        edgeValue.setCreatedAt(createdAt);
        return edgeValueRepository.save(edgeValue);
    }

    @Transactional(readOnly = true)
    public Optional<EdgeValueEntity> getCurrentValue(Long edgeId, OffsetDateTime now) {
        return edgeValueRepository.findCurrentValueByEdgeId(edgeId, now);
    }

    @Transactional(readOnly = true)
    public String getCurrentValueText(Long edgeId, OffsetDateTime now) {
        return getCurrentValue(edgeId, now).map(EdgeValueEntity::getValue).orElse(null);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getCurrentValues(OffsetDateTime now) {
        return edgeValueRepository.findCurrentValues(now).stream()
            .collect(Collectors.toMap(value -> value.getEdge().getId(), EdgeValueEntity::getValue, (a, b) -> a));
    }

    @Transactional(readOnly = true)
    public Map<Long, EdgeValueEntity> getCurrentValueEntities(OffsetDateTime now) {
        return edgeValueRepository.findCurrentValues(now).stream()
            .collect(Collectors.toMap(value -> value.getEdge().getId(), value -> value, (a, b) -> a));
    }

    @Transactional(readOnly = true)
    public List<String> getCurrentPublicValues(OffsetDateTime now) {
        return edgeValueRepository.findDistinctCurrentPublicValues(now);
    }
}
