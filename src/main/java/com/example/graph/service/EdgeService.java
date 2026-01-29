package com.example.graph.service;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.model.ValueEntity;
import com.example.graph.repository.EdgeRepository;
import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.ValueRepository;
import com.example.graph.web.dto.EdgeDto;
import com.example.graph.web.dto.ValueDto;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EdgeService {
    private final EdgeRepository edgeRepository;
    private final NodeRepository nodeRepository;
    private final ValueRepository valueRepository;

    public EdgeService(EdgeRepository edgeRepository, NodeRepository nodeRepository, ValueRepository valueRepository) {
        this.edgeRepository = edgeRepository;
        this.nodeRepository = nodeRepository;
        this.valueRepository = valueRepository;
    }

    public EdgeEntity createEdge(Long fromId, Long toId, Long labelId, String newLabel,
                                 LocalDateTime createdAt, LocalDateTime expiredAt) {
        if (toId == null) {
            throw new IllegalArgumentException("To node is required.");
        }
        NodeEntity fromNode = null;
        if (fromId != null) {
            if (fromId.equals(toId)) {
                throw new IllegalArgumentException("Self-loops are not allowed.");
            }
            fromNode = nodeRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("From node not found."));
        }
        NodeEntity toNode = nodeRepository.findById(toId)
            .orElseThrow(() -> new IllegalArgumentException("To node not found."));
        if (fromId != null && edgeRepository.existsByFromNodeIdAndToNodeId(fromId, toId)) {
            throw new IllegalArgumentException("That edge already exists.");
        }
        OffsetDateTime createdAtOffset = toOffsetDateTime(createdAt);
        OffsetDateTime expiredAtOffset = toOffsetDateTime(expiredAt);
        if (createdAtOffset != null && expiredAtOffset != null && createdAtOffset.isAfter(expiredAtOffset)) {
            throw new IllegalArgumentException("Created time must be before expired time.");
        }

        EdgeEntity edge = new EdgeEntity();
        edge.setFromNode(fromNode);
        edge.setToNode(toNode);
        edge.setCreatedAt(createdAtOffset);
        edge.setExpiredAt(expiredAtOffset);

        ValueEntity value = resolveValue(labelId, newLabel);
        if (value != null) {
            edge.setValue(value);
        }
        return edgeRepository.save(edge);
    }

    private ValueEntity resolveValue(Long labelId, String newLabel) {
        String trimmedLabel = newLabel == null ? null : newLabel.trim();
        if (trimmedLabel != null && !trimmedLabel.isEmpty()) {
            if (trimmedLabel.length() > 200) {
                throw new IllegalArgumentException("New label must be between 1 and 200 characters.");
            }
            ValueEntity valueEntity = new ValueEntity();
            valueEntity.setText(trimmedLabel);
            valueEntity.setCreatedAt(OffsetDateTime.now());
            return valueRepository.save(valueEntity);
        }
        if (newLabel != null && !newLabel.isEmpty() && trimmedLabel != null && trimmedLabel.isEmpty()) {
            throw new IllegalArgumentException("New label must be between 1 and 200 characters.");
        }
        if (labelId != null) {
            ValueEntity label = edgeRepository.findDistinctPublicEdgeValues().stream()
                .filter(candidate -> labelId.equals(candidate.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Label is not a public edge label."));
            return label;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<EdgeDto> listEdgesDto() {
        return edgeRepository.findAll().stream()
            .map(this::toDto)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<EdgeDto> getPublicEdges() {
        return edgeRepository.findAllByFromNodeIsNull().stream()
            .map(this::toDto)
            .toList();
    }

    public void deleteEdge(Long id) {
        edgeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ValueDto> getPublicEdgeValues() {
        return edgeRepository.findDistinctPublicEdgeValues().stream()
            .map(value -> new ValueDto(value.getId(), value.getText()))
            .sorted(Comparator.comparing(ValueDto::getText, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    private OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    private EdgeDto toDto(EdgeEntity edge) {
        NodeEntity fromNode = edge.getFromNode();
        NodeEntity toNode = edge.getToNode();
        return new EdgeDto(
            edge.getId(),
            fromNode == null ? null : fromNode.getId(),
            toNode.getId(),
            edge.getValue() == null ? null : edge.getValue().getText(),
            edge.getCreatedAt(),
            edge.getExpiredAt(),
            fromNode == null ? null : fromNode.getValue().getText(),
            toNode.getValue().getText()
        );
    }
}
