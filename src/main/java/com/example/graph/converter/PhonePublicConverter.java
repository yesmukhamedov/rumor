package com.example.graph.converter;

import com.example.graph.model.NodeEntity;
import com.example.graph.model.phone.PhoneEntity;
import com.example.graph.model.phone.PhonePatternEntity;
import com.example.graph.model.phone.PhoneValueEntity;
import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.PhonePatternRepository;
import com.example.graph.repository.PhoneRepository;
import com.example.graph.validate.ValidationException;
import com.example.graph.web.form.PhonePublicForm;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;

@Component
public class PhonePublicConverter {
    private final NodeRepository nodeRepository;
    private final PhoneRepository phoneRepository;
    private final PhonePatternRepository phonePatternRepository;

    public PhonePublicConverter(NodeRepository nodeRepository,
                                PhoneRepository phoneRepository,
                                PhonePatternRepository phonePatternRepository) {
        this.nodeRepository = nodeRepository;
        this.phoneRepository = phoneRepository;
        this.phonePatternRepository = phonePatternRepository;
    }

    public PhoneEntity toEntity(PhonePublicForm form) {
        NodeEntity node = nodeRepository.findById(form.getNodeId())
            .orElseThrow(() -> new ValidationException("Node not found."));
        return phoneRepository.findByNodeId(form.getNodeId())
            .orElseGet(() -> {
                PhoneEntity phone = new PhoneEntity();
                phone.setNode(node);
                return phoneRepository.save(phone);
            });
    }

    public PhoneValueEntity toValueEntity(PhoneEntity phone, PhonePublicForm form, OffsetDateTime now) {
        PhonePatternEntity pattern = phonePatternRepository.findById(form.getPatternId())
            .orElseThrow(() -> new ValidationException("Pattern not found."));
        PhoneValueEntity value = new PhoneValueEntity();
        value.setPhone(phone);
        value.setPattern(pattern);
        value.setValue(form.getValue().trim());
        value.setCreatedAt(now);
        return value;
    }
}
