package com.example.graph.validate;

import com.example.graph.repository.EdgeRepository;
import com.example.graph.repository.NodeRepository;
import com.example.graph.web.form.EdgePublicForm;
import com.example.graph.web.form.EdgeValueForm;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;

@Component
public class EdgePublicValidator {
    private static final int MAX_VALUE_LENGTH = 200;

    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;

    public EdgePublicValidator(NodeRepository nodeRepository, EdgeRepository edgeRepository) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
    }

    public void validate(EdgePublicForm form, String fieldPrefix, ValidationErrorCollector errors) {
        if (form == null) {
            errors.add(fieldPrefix, "Edge data is required.");
            return;
        }
        if (form.getFromNodeId() == null && form.getToNodeId() == null) {
            errors.add(fieldPrefix, "Edge cannot be both PUBLIC and PRIVATE.");
        }
        if (form.getFromNodeId() != null && form.getToNodeId() != null
            && form.getFromNodeId().equals(form.getToNodeId())) {
            errors.add(fieldPrefix, "Self-loops are not allowed.");
        }
        if (form.getCreatedAt() != null && form.getExpiredAt() != null
            && form.getCreatedAt().isAfter(form.getExpiredAt())) {
            errors.add(fieldPrefix + ".createdAt", "Created time must be before expired time.");
        }
        if (form.getFromNodeId() != null && !nodeRepository.existsById(form.getFromNodeId())) {
            errors.add(fieldPrefix + ".fromNodeId", "From node not found.");
        }
        if (form.getToNodeId() != null && !nodeRepository.existsById(form.getToNodeId())) {
            errors.add(fieldPrefix + ".toNodeId", "To node not found.");
        }
        if (form.getValue() != null) {
            validateValue(form.getValue(), fieldPrefix + ".value", errors);
        }
    }

    public void validate(EdgeValueForm form, String fieldPrefix, ValidationErrorCollector errors) {
        if (form == null) {
            errors.add(fieldPrefix, "Edge value is required.");
            return;
        }
        if (form.getEdgeId() == null) {
            errors.add(fieldPrefix + ".edgeId", "Edge is required.");
        } else if (!edgeRepository.existsById(form.getEdgeId())) {
            errors.add(fieldPrefix + ".edgeId", "Edge not found.");
        }
        validateValue(form, fieldPrefix, errors);
    }

    private void validateValue(EdgeValueForm form, String fieldPrefix, ValidationErrorCollector errors) {
        boolean hasValue = form.getValue() != null && !form.getValue().isBlank();
        boolean hasBody = form.getBody() != null && !form.getBody().isBlank();
        boolean hasRelationType = form.getRelationType() != null && !form.getRelationType().isBlank();
        if (!hasValue && !hasBody && !hasRelationType) {
            errors.add(fieldPrefix, "Edge value, relation type, or body is required.");
        }
        if (hasValue && form.getValue().trim().length() > MAX_VALUE_LENGTH) {
            errors.add(fieldPrefix + ".value", "Edge value must be at most 200 characters.");
        }
        if (hasRelationType && form.getRelationType().trim().length() > MAX_VALUE_LENGTH) {
            errors.add(fieldPrefix + ".relationType", "Relation type must be at most 200 characters.");
        }
        OffsetDateTime effectiveAt = form.getEffectiveAt();
        if (effectiveAt != null && effectiveAt.isAfter(OffsetDateTime.now())) {
            errors.add(fieldPrefix + ".effectiveAt", "Effective time cannot be in the future.");
        }
    }
}
