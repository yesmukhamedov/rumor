package com.example.graph.validate;

import com.example.graph.repository.NodeRepository;
import com.example.graph.web.form.UserPublicForm;
import org.springframework.stereotype.Component;

@Component
public class UserPublicValidator {
    private final NodeRepository nodeRepository;

    public UserPublicValidator(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    public void validate(UserPublicForm form, String fieldPrefix, ValidationErrorCollector errors) {
        if (form == null) {
            errors.add(fieldPrefix, "User data is required.");
            return;
        }
        if (form.getNodeId() == null) {
            errors.add(fieldPrefix + ".nodeId", "Node is required.");
        } else if (!nodeRepository.existsById(form.getNodeId())) {
            errors.add(fieldPrefix + ".nodeId", "Node not found.");
        }
    }
}
