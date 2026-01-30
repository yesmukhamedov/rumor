package com.example.graph.service;

import com.example.graph.model.NodeEntity;
import com.example.graph.model.PhoneEntity;
import com.example.graph.model.PhonePatternEntity;
import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.PhonePatternRepository;
import com.example.graph.repository.PhoneRepository;
import com.example.graph.web.dto.PhoneDto;
import com.example.graph.web.dto.PhonePatternDto;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PhoneService {
    private final PhoneRepository phoneRepository;
    private final PhonePatternRepository phonePatternRepository;
    private final NodeRepository nodeRepository;
    private final PhoneValueService phoneValueService;
    private final NodeValueService nodeValueService;

    public PhoneService(PhoneRepository phoneRepository,
                        PhonePatternRepository phonePatternRepository,
                        NodeRepository nodeRepository,
                        PhoneValueService phoneValueService,
                        NodeValueService nodeValueService) {
        this.phoneRepository = phoneRepository;
        this.phonePatternRepository = phonePatternRepository;
        this.nodeRepository = nodeRepository;
        this.phoneValueService = phoneValueService;
        this.nodeValueService = nodeValueService;
    }

    public PhoneEntity createPhone(Long nodeId, Long patternId, String value) {
        if (nodeId == null) {
            throw new IllegalArgumentException("Node is required.");
        }
        if (patternId == null) {
            throw new IllegalArgumentException("Pattern is required.");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Value is required.");
        }
        NodeEntity node = nodeRepository.findById(nodeId)
            .orElseThrow(() -> new IllegalArgumentException("Node not found."));
        PhonePatternEntity pattern = phonePatternRepository.findById(patternId)
            .orElseThrow(() -> new IllegalArgumentException("Pattern not found."));
        if (phoneRepository.existsByNodeId(nodeId)) {
            throw new IllegalArgumentException("Selected node already has a phone.");
        }
        if (phoneValueService.existsByValue(value)) {
            throw new IllegalArgumentException("Phone value already exists.");
        }
        validateMaskedValue(value, pattern.getValue());
        PhoneEntity phone = new PhoneEntity();
        phone.setPattern(pattern);
        phone.setNode(node);
        PhoneEntity savedPhone = phoneRepository.save(phone);
        phoneValueService.createCurrentValue(savedPhone, value, java.time.OffsetDateTime.now());
        return savedPhone;
    }

    public void deletePhone(Long id) {
        phoneRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PhoneDto> listPhonesDto() {
        java.time.OffsetDateTime now = java.time.OffsetDateTime.now();
        Map<Long, String> nodeNames = nodeValueService.getCurrentValues(now);
        Map<Long, String> phoneValues = phoneValueService.getCurrentValues(now);
        return phoneRepository.findAll().stream()
            .map(phone -> new PhoneDto(
                phone.getId(),
                nodeNames.getOrDefault(phone.getNode().getId(), "—"),
                phone.getPattern().getCode(),
                phoneValues.get(phone.getId())
            ))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<PhonePatternDto> listPatternsDto() {
        return phonePatternRepository.findAll().stream()
            .map(pattern -> new PhonePatternDto(pattern.getId(), pattern.getCode(), pattern.getValue()))
            .sorted(Comparator.comparing(PhonePatternDto::getCode, String.CASE_INSENSITIVE_ORDER))
            .toList();
    }

    private void validateMaskedValue(String value, String mask) {
        if (value.length() != mask.length()) {
            throw new IllegalArgumentException("Phone value does not match the selected pattern.");
        }
        for (int i = 0; i < mask.length(); i++) {
            char maskChar = mask.charAt(i);
            char valueChar = value.charAt(i);
            if (maskChar == '_') {
                if (!Character.isDigit(valueChar)) {
                    throw new IllegalArgumentException("Phone value does not match the selected pattern.");
                }
            } else if (valueChar != maskChar) {
                throw new IllegalArgumentException("Phone value does not match the selected pattern.");
            }
        }
    }
}
