package com.example.graph.service;

import com.example.graph.model.PhoneEntity;
import com.example.graph.model.PhoneValueEntity;
import com.example.graph.repository.PhoneValueRepository;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PhoneValueService {
    private final PhoneValueRepository phoneValueRepository;

    public PhoneValueService(PhoneValueRepository phoneValueRepository) {
        this.phoneValueRepository = phoneValueRepository;
    }

    public boolean existsByValue(String value) {
        return phoneValueRepository.existsByValue(value);
    }

    public PhoneValueEntity createCurrentValue(PhoneEntity phone, String value, OffsetDateTime createdAt) {
        PhoneValueEntity phoneValue = new PhoneValueEntity();
        phoneValue.setPhone(phone);
        phoneValue.setValue(value);
        phoneValue.setCreatedAt(createdAt);
        return phoneValueRepository.save(phoneValue);
    }

    @Transactional(readOnly = true)
    public Optional<PhoneValueEntity> getCurrentValue(Long phoneId, OffsetDateTime now) {
        return phoneValueRepository.findCurrentValueByPhoneId(phoneId, now);
    }

    @Transactional(readOnly = true)
    public String getCurrentValueText(Long phoneId, OffsetDateTime now) {
        return getCurrentValue(phoneId, now).map(PhoneValueEntity::getValue).orElse(null);
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getCurrentValues(OffsetDateTime now) {
        return phoneValueRepository.findCurrentValues(now).stream()
            .collect(Collectors.toMap(value -> value.getPhone().getId(), PhoneValueEntity::getValue, (a, b) -> a));
    }
}
