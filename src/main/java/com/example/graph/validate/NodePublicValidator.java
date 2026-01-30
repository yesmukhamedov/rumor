package com.example.graph.validate;

import com.example.graph.repository.NodeRepository;
import com.example.graph.web.form.NodePublicForm;
import com.example.graph.web.form.NodeValueForm;
import org.springframework.stereotype.Component;

@Component
public class NodePublicValidator {
    private static final int MAX_VALUE_LENGTH = 200;

    private final NodeRepository nodeRepository;

    public NodePublicValidator(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    public void validate(NodePublicForm form) {
        if (form == null) {
            throw new ValidationException("Node data is required.");
        }
        if (form.getId() == null && form.getValue() == null) {
            throw new ValidationException("Node value is required.");
        }
        if (form.getValue() != null) {
            validateValue(form.getValue());
        }
    }

    public void validate(NodeValueForm form) {
        if (form == null) {
            throw new ValidationException("Node value is required.");
        }
        if (form.getNodeId() == null) {
            throw new ValidationException("Node is required.");
        }
        validateValue(form);
        if (!nodeRepository.existsById(form.getNodeId())) {
            throw new ValidationException("Node not found.");
        }
    }

    private void validateValue(NodeValueForm form) {
        if (form.getValue() == null || form.getValue().isBlank()) {
            throw new ValidationException("Node value is required.");
        }
        if (form.getValue().trim().length() > MAX_VALUE_LENGTH) {
            throw new ValidationException("Node value must be at most 200 characters.");
        }
    }
}
