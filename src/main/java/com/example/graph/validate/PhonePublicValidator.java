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

    public void validate(PhonePublicForm form) {
        if (form == null) {
            throw new ValidationException("Phone data is required.");
        }
        if (form.getNodeId() == null) {
            throw new ValidationException("Node is required.");
        }
        if (form.getPatternId() == null) {
            throw new ValidationException("Pattern is required.");
        }
        if (form.getValue() == null || form.getValue().isBlank()) {
            throw new ValidationException("Digits are required.");
        }
        if (form.getValue().trim().length() > MAX_VALUE_LENGTH) {
            throw new ValidationException("Digits must be at most 32 characters.");
        }
        if (!nodeRepository.existsById(form.getNodeId())) {
            throw new ValidationException("Node not found.");
        }
        var pattern = phonePatternRepository.findById(form.getPatternId())
            .orElseThrow(() -> new ValidationException("Pattern not found."));
        phoneDigitsValidator.validateDigitsAgainstPattern(form.getValue().trim(), pattern);
        if (phoneValueRepository.existsByValue(form.getValue().trim())) {
            throw new ValidationException("Phone value already exists.");
        }
    }
}
