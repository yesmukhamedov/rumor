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

    public void validate(EdgePublicForm form) {
        if (form == null) {
            throw new ValidationException("Edge data is required.");
        }
        if (form.getFromNodeId() == null && form.getToNodeId() == null) {
            throw new ValidationException("Edge cannot be both PUBLIC and PRIVATE.");
        }
        if (form.getFromNodeId() != null && form.getToNodeId() != null
            && form.getFromNodeId().equals(form.getToNodeId())) {
            throw new ValidationException("Self-loops are not allowed.");
        }
        if (form.getCreatedAt() != null && form.getExpiredAt() != null
            && form.getCreatedAt().isAfter(form.getExpiredAt())) {
            throw new ValidationException("Created time must be before expired time.");
        }
        if (form.getFromNodeId() != null && !nodeRepository.existsById(form.getFromNodeId())) {
            throw new ValidationException("From node not found.");
        }
        if (form.getToNodeId() != null && !nodeRepository.existsById(form.getToNodeId())) {
            throw new ValidationException("To node not found.");
        }
        if (form.getValue() != null) {
            validateValue(form.getValue());
        }
    }

    public void validate(EdgeValueForm form) {
        if (form == null) {
            throw new ValidationException("Edge value is required.");
        }
        if (form.getEdgeId() == null) {
            throw new ValidationException("Edge is required.");
        }
        validateValue(form);
        if (!edgeRepository.existsById(form.getEdgeId())) {
            throw new ValidationException("Edge not found.");
        }
    }

    private void validateValue(EdgeValueForm form) {
        boolean hasValue = form.getValue() != null && !form.getValue().isBlank();
        boolean hasBody = form.getBody() != null && !form.getBody().isBlank();
        if (!hasValue && !hasBody) {
            throw new ValidationException("Edge value or body is required.");
        }
        if (hasValue && form.getValue().trim().length() > MAX_VALUE_LENGTH) {
            throw new ValidationException("Edge value must be at most 200 characters.");
        }
        OffsetDateTime effectiveAt = form.getEffectiveAt();
        if (effectiveAt != null && effectiveAt.isAfter(OffsetDateTime.now())) {
            throw new ValidationException("Effective time cannot be in the future.");
        }
    }
}
