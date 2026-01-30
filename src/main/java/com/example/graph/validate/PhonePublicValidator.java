package com.example.graph.validate;

import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.PhonePatternRepository;
import com.example.graph.repository.PhoneValueRepository;
import com.example.graph.web.form.PhonePublicForm;
import org.springframework.stereotype.Component;

@Component
public class PhonePublicValidator {
    private static final int MAX_VALUE_LENGTH = 32;

    private final NodeRepository nodeRepository;
    private final PhonePatternRepository phonePatternRepository;
    private final PhoneValueRepository phoneValueRepository;
    private final PhoneDigitsValidator phoneDigitsValidator;

    public PhonePublicValidator(NodeRepository nodeRepository,
                                PhonePatternRepository phonePatternRepository,
                                PhoneValueRepository phoneValueRepository,
                                PhoneDigitsValidator phoneDigitsValidator) {
        this.nodeRepository = nodeRepository;
        this.phonePatternRepository = phonePatternRepository;
        this.phoneValueRepository = phoneValueRepository;
        this.phoneDigitsValidator = phoneDigitsValidator;
    }

    public void validate(PhonePublicForm form, String fieldPrefix, ValidationErrorCollector errors) {
        if (form == null) {
            errors.add(fieldPrefix, "Phone data is required.");
            return;
        }
        if (form.getNodeId() == null) {
            errors.add(fieldPrefix + ".nodeId", "Node is required.");
        } else if (!nodeRepository.existsById(form.getNodeId())) {
            errors.add(fieldPrefix + ".nodeId", "Node not found.");
        }
        if (form.getPatternId() == null) {
            errors.add(fieldPrefix + ".patternId", "Pattern is required.");
        }
        String normalized = form.getValue() == null ? null : form.getValue().trim();
        if (normalized == null || normalized.isBlank()) {
            errors.add(fieldPrefix + ".value", "Digits are required.");
        } else if (normalized.length() > MAX_VALUE_LENGTH) {
            errors.add(fieldPrefix + ".value", "Digits must be at most 32 characters.");
        }
        if (form.getPatternId() != null) {
            phonePatternRepository.findById(form.getPatternId()).ifPresentOrElse(
                pattern -> phoneDigitsValidator.validateDigitsAgainstPattern(normalized, pattern,
                    fieldPrefix + ".value", errors),
                () -> errors.add(fieldPrefix + ".patternId", "Pattern not found.")
            );
        }
        if (normalized != null && !normalized.isBlank() && phoneValueRepository.existsByValue(normalized)) {
            errors.add(fieldPrefix + ".value", "Phone value already exists.");
        }
    }
}
