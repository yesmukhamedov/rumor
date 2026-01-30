package com.example.graph.service;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.repository.EdgeRepository;
import com.example.graph.repository.NodeRepository;
import com.example.graph.web.dto.EdgeDto;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EdgeService {
    private final EdgeRepository edgeRepository;
    private final NodeRepository nodeRepository;
    private final EdgeValueService edgeValueService;
    private final NodeValueService nodeValueService;

    public EdgeService(EdgeRepository edgeRepository,
                       NodeRepository nodeRepository,
                       EdgeValueService edgeValueService,
                       NodeValueService nodeValueService) {
        this.edgeRepository = edgeRepository;
        this.nodeRepository = nodeRepository;
        this.edgeValueService = edgeValueService;
        this.nodeValueService = nodeValueService;
    }

    public EdgeEntity createEdge(Long fromId, Long toId, String labelValue, String newLabel,
                                 LocalDateTime createdAt, LocalDateTime expiredAt) {
        if (fromId == null && toId == null) {
            throw new IllegalArgumentException("Edge cannot be both PUBLIC and PRIVATE.");
        }
        if (fromId != null && toId != null && fromId.equals(toId)) {
            throw new IllegalArgumentException("Self-loops are not allowed.");
        }
        NodeEntity fromNode = null;
        if (fromId != null) {
            fromNode = nodeRepository.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("From node not found."));
        }
        NodeEntity toNode = null;
        if (toId != null) {
            toNode = nodeRepository.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("To node not found."));
        }
        if (fromId != null && toId != null && edgeRepository.existsByFromNodeIdAndToNodeId(fromId, toId)) {
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

        String label = resolveLabelValue(labelValue, newLabel);
        if (labelValue != null && !labelValue.isBlank() && (newLabel == null || newLabel.isBlank())) {
            List<String> currentPublicLabels = edgeValueService.getCurrentPublicValues(OffsetDateTime.now());
            if (currentPublicLabels.stream().noneMatch(value -> value.equals(label))) {
                throw new IllegalArgumentException("Label is not a public edge label.");
            }
        }
        EdgeEntity savedEdge = edgeRepository.save(edge);
        if (label != null) {
            edgeValueService.createCurrentValue(savedEdge, label, OffsetDateTime.now());
        }
        return savedEdge;
    }

    private String resolveLabelValue(String labelValue, String newLabel) {
        String trimmedLabel = newLabel == null ? null : newLabel.trim();
        if (trimmedLabel != null && !trimmedLabel.isEmpty()) {
            if (trimmedLabel.length() > 200) {
                throw new IllegalArgumentException("New label must be between 1 and 200 characters.");
            }
            return trimmedLabel;
        }
        if (newLabel != null && !newLabel.isEmpty() && trimmedLabel != null && trimmedLabel.isEmpty()) {
            throw new IllegalArgumentException("New label must be between 1 and 200 characters.");
        }
        if (labelValue != null && !labelValue.isBlank()) {
            String trimmedValue = labelValue.trim();
            if (trimmedValue.isEmpty() || trimmedValue.length() > 200) {
                throw new IllegalArgumentException("Label must be between 1 and 200 characters.");
            }
            return trimmedValue;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<EdgeDto> listEdgesDto() {
        OffsetDateTime now = OffsetDateTime.now();
        Map<Long, String> edgeLabels = edgeValueService.getCurrentValues(now);
        Map<Long, String> nodeNames = nodeValueService.getCurrentValues(now);
        return edgeRepository.findAll().stream()
            .map(edge -> toDto(edge, edgeLabels, nodeNames))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<EdgeDto> getPublicEdges() {
        OffsetDateTime now = OffsetDateTime.now();
        Map<Long, String> edgeLabels = edgeValueService.getCurrentValues(now);
        Map<Long, String> nodeNames = nodeValueService.getCurrentValues(now);
        return edgeRepository.findAllByFromNodeIsNull().stream()
            .filter(EdgeEntity::isCategory)
            .map(edge -> toDto(edge, edgeLabels, nodeNames))
            .toList();
    }

    public void deleteEdge(Long id) {
        edgeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<String> getPublicEdgeValues() {
        return edgeValueService.getCurrentPublicValues(OffsetDateTime.now()).stream()
            .sorted(String.CASE_INSENSITIVE_ORDER)
            .toList();
    }

    private OffsetDateTime toOffsetDateTime(LocalDateTime value) {
        if (value == null) {
            return null;
        }
        return value.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    private EdgeDto toDto(EdgeEntity edge, Map<Long, String> edgeLabels, Map<Long, String> nodeNames) {
        NodeEntity fromNode = edge.getFromNode();
        NodeEntity toNode = edge.getToNode();
        return new EdgeDto(
            edge.getId(),
            fromNode == null ? null : fromNode.getId(),
            toNode == null ? null : toNode.getId(),
            edgeLabels.get(edge.getId()),
            edge.getCreatedAt(),
            edge.getExpiredAt(),
            fromNode == null ? null : nodeNames.get(fromNode.getId()),
            toNode == null ? null : nodeNames.get(toNode.getId()),
            edge.isCategory(),
            edge.isNote(),
            edge.isRelation(),
            edge.isInvalid()
        );
    }
}
