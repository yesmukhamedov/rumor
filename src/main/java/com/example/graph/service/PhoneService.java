package com.example.graph.service;

import com.example.graph.model.PhoneEntity;
import com.example.graph.model.PhonePatternEntity;
import com.example.graph.repository.PhonePatternRepository;
import com.example.graph.repository.PhoneRepository;
import com.example.graph.web.dto.PhoneDto;
import com.example.graph.web.dto.PhonePatternDto;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PhoneService {
    private final PhoneRepository phoneRepository;
    private final PhonePatternRepository phonePatternRepository;

    public PhoneService(PhoneRepository phoneRepository, PhonePatternRepository phonePatternRepository) {
        this.phoneRepository = phoneRepository;
        this.phonePatternRepository = phonePatternRepository;
    }

    public PhoneEntity createPhone(Long patternId, String value) {
        if (patternId == null) {
            throw new IllegalArgumentException("Pattern is required.");
        }
        PhonePatternEntity pattern = phonePatternRepository.findById(patternId)
            .orElseThrow(() -> new IllegalArgumentException("Pattern not found."));
        PhoneEntity phone = new PhoneEntity();
        phone.setPattern(pattern);
        phone.setValue(value);
        return phoneRepository.save(phone);
    }

    public void deletePhone(Long id) {
        phoneRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PhoneDto> listPhonesDto() {
        return phoneRepository.findAll().stream()
            .map(phone -> new PhoneDto(
                phone.getId(),
                phone.getPattern().getCode(),
                phone.getPattern().getValue(),
                phone.getValue()
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
}
