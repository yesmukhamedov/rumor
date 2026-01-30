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

    public void validate(NodePublicForm form, String fieldPrefix, ValidationErrorCollector errors) {
        if (form == null) {
            errors.add(fieldPrefix, "Node data is required.");
            return;
        }
        if (form.getId() == null && form.getValue() == null) {
            errors.add(fieldPrefix + ".value", "Node value is required.");
        }
        if (form.getValue() != null) {
            validateValue(form.getValue(), fieldPrefix + ".value", errors);
        }
    }

    public void validate(NodeValueForm form, String fieldPrefix, ValidationErrorCollector errors) {
        if (form == null) {
            errors.add(fieldPrefix, "Node value is required.");
            return;
        }
        if (form.getNodeId() == null) {
            errors.add(fieldPrefix + ".nodeId", "Node is required.");
        } else if (!nodeRepository.existsById(form.getNodeId())) {
            errors.add(fieldPrefix + ".nodeId", "Node not found.");
        }
        validateValue(form, fieldPrefix, errors);
    }

    private void validateValue(NodeValueForm form, String fieldPrefix, ValidationErrorCollector errors) {
        if (form.getValue() == null || form.getValue().isBlank()) {
            errors.add(fieldPrefix + ".value", "Node value is required.");
        } else if (form.getValue().trim().length() > MAX_VALUE_LENGTH) {
            errors.add(fieldPrefix + ".value", "Node value must be at most 200 characters.");
        }
    }
}
