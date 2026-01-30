package com.example.graph.converter;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.model.value.EdgeValueEntity;
import com.example.graph.repository.EdgeRepository;
import com.example.graph.repository.NodeRepository;
import com.example.graph.validate.ValidationException;
import com.example.graph.web.form.EdgePublicForm;
import com.example.graph.web.form.EdgeValueForm;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;

@Component
public class EdgePublicConverter {
    private final EdgeRepository edgeRepository;
    private final NodeRepository nodeRepository;

    public EdgePublicConverter(EdgeRepository edgeRepository, NodeRepository nodeRepository) {
        this.edgeRepository = edgeRepository;
        this.nodeRepository = nodeRepository;
    }

    public EdgeEntity toEntity(EdgePublicForm form) {
        EdgeEntity edge = null;
        if (form.getId() != null) {
            edge = edgeRepository.findById(form.getId()).orElse(null);
        }
        if (edge == null && form.getFromNodeId() != null && form.getToNodeId() != null) {
            edge = edgeRepository.findByFromNodeIdAndToNodeId(form.getFromNodeId(), form.getToNodeId());
        }
        if (edge == null) {
            edge = new EdgeEntity();
        }
        edge.setFromNode(resolveEdgeNode(form.getFromNodeId(), "From node not found."));
        edge.setToNode(resolveEdgeNode(form.getToNodeId(), "To node not found."));
        edge.setCreatedAt(form.getCreatedAt());
        edge.setExpiredAt(form.getExpiredAt());
        return edgeRepository.save(edge);
    }

    public EdgeValueEntity toValueEntity(EdgeEntity edge, EdgeValueForm form, OffsetDateTime now) {
        EdgeValueEntity value = new EdgeValueEntity();
        value.setEdge(edge);
        if (form.getValue() != null) {
            value.setValue(form.getValue().trim());
        }
        value.setBody(normalize(form.getBody()));
        value.setCreatedAt(resolveEffectiveAt(form.getEffectiveAt(), now));
        return value;
    }

    private NodeEntity resolveEdgeNode(Long nodeId, String message) {
        if (nodeId == null) {
            return null;
        }
        return nodeRepository.findById(nodeId)
            .orElseThrow(() -> new ValidationException(message));
    }

    private OffsetDateTime resolveEffectiveAt(OffsetDateTime effectiveAt, OffsetDateTime fallback) {
        return effectiveAt == null ? fallback : effectiveAt;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
