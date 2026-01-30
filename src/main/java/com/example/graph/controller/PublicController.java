package com.example.graph.controller;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.EdgeValueEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.model.NodeValueEntity;
import com.example.graph.model.PhoneEntity;
import com.example.graph.model.PhonePatternEntity;
import com.example.graph.model.PhoneValueEntity;
import com.example.graph.repository.EdgeRepository;
import com.example.graph.repository.EdgeValueRepository;
import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.NodeValueRepository;
import com.example.graph.repository.PhonePatternRepository;
import com.example.graph.repository.PhoneRepository;
import com.example.graph.repository.PhoneValueRepository;
import com.example.graph.service.PublicGraphService;
import com.example.graph.web.dto.EdgePublicForm;
import com.example.graph.web.dto.EdgeValueForm;
import com.example.graph.web.dto.NodePublicForm;
import com.example.graph.web.dto.NodeValueForm;
import com.example.graph.web.dto.PhonePublicForm;
import com.example.graph.web.dto.PublicGraphPostRequest;
import com.example.graph.web.dto.PublicValuesPatchRequest;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/public", produces = "application/ld+json")
public class PublicController {
    private final PublicGraphService publicGraphService;
    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;
    private final PhoneRepository phoneRepository;
    private final PhonePatternRepository phonePatternRepository;
    private final NodeValueRepository nodeValueRepository;
    private final EdgeValueRepository edgeValueRepository;
    private final PhoneValueRepository phoneValueRepository;

    public PublicController(PublicGraphService publicGraphService,
                            NodeRepository nodeRepository,
                            EdgeRepository edgeRepository,
                            PhoneRepository phoneRepository,
                            PhonePatternRepository phonePatternRepository,
                            NodeValueRepository nodeValueRepository,
                            EdgeValueRepository edgeValueRepository,
                            PhoneValueRepository phoneValueRepository) {
        this.publicGraphService = publicGraphService;
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
        this.phoneRepository = phoneRepository;
        this.phonePatternRepository = phonePatternRepository;
        this.nodeValueRepository = nodeValueRepository;
        this.edgeValueRepository = edgeValueRepository;
        this.phoneValueRepository = phoneValueRepository;
    }

    @GetMapping("/graph")
    public ResponseEntity<Map<String, Object>> getGraph(@RequestParam Map<String, String> params) {
        Long nodeId = parseLong(params.get("nodeId"), "nodeId");
        OffsetDateTime at = parseOffsetDateTime(params.get("at"));
        return ResponseEntity.ok(publicGraphService.buildGraph(nodeId, at));
    }

    @PostMapping(path = "/graph", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Map<String, Object>> postGraph(@RequestBody PublicGraphPostRequest request) {
        List<NodePublicForm> nodes = request.getNodes() == null ? Collections.emptyList() : request.getNodes();
        List<EdgePublicForm> edges = request.getEdges() == null ? Collections.emptyList() : request.getEdges();
        List<PhonePublicForm> phones = request.getPhones() == null ? Collections.emptyList() : request.getPhones();

        OffsetDateTime now = OffsetDateTime.now();
        for (NodePublicForm form : nodes) {
            validateNodeForm(form);
            NodeEntity node = resolveNode(form.getId());
            NodeValueEntity value = new NodeValueEntity();
            value.setNode(node);
            value.setValue(form.getValue().trim());
            value.setCreatedAt(now);
            value.setCreatedBy(normalize(form.getCreatedBy()));
            nodeValueRepository.save(value);
        }

        for (EdgePublicForm form : edges) {
            validateEdgeForm(form);
            EdgeEntity edge = resolveEdge(form);
            if (form.getValue() != null && !form.getValue().isBlank()) {
                updateEdgeValue(edge, form.getValue().trim(), now, form.getCreatedBy());
            }
        }

        for (PhonePublicForm form : phones) {
            validatePhoneForm(form);
            PhoneEntity phone = resolvePhone(form);
            if (phoneValueRepository.existsByValue(form.getValue())) {
                throw new IllegalArgumentException("Phone value already exists.");
            }
            updatePhoneValue(phone, form.getValue().trim(), now, form.getCreatedBy());
        }

        return ResponseEntity.ok(publicGraphService.buildGraph(null, null));
    }

    @PatchMapping(path = "/values", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Map<String, Object>> patchValues(@RequestBody PublicValuesPatchRequest request) {
        if (request.getNodeValue() != null) {
            applyNodeValueUpdate(request.getNodeValue());
        }
        if (request.getEdgeValue() != null) {
            applyEdgeValueUpdate(request.getEdgeValue());
        }
        return ResponseEntity.ok(publicGraphService.buildGraph(null, null));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    private NodeEntity resolveNode(Long id) {
        if (id == null) {
            return nodeRepository.save(new NodeEntity());
        }
        return nodeRepository.findById(id).orElseGet(() -> nodeRepository.save(new NodeEntity()));
    }

    private EdgeEntity resolveEdge(EdgePublicForm form) {
        EdgeEntity edge = null;
        if (form.getId() != null) {
            edge = edgeRepository.findById(form.getId()).orElse(null);
        }
        if (edge == null && form.getFromNodeId() != null && form.getToNodeId() != null) {
            edge = edgeRepository.findByFromNodeIdAndToNodeId(form.getFromNodeId(), form.getToNodeId());
        }
        if (edge == null) {
            edge = new EdgeEntity();
        }
        edge.setFromNode(resolveEdgeNode(form.getFromNodeId(), "From node not found."));
        edge.setToNode(resolveEdgeNode(form.getToNodeId(), "To node not found."));
        edge.setCreatedAt(form.getCreatedAt());
        edge.setExpiredAt(form.getExpiredAt());
        return edgeRepository.save(edge);
    }

    private NodeEntity resolveEdgeNode(Long nodeId, String message) {
        if (nodeId == null) {
            return null;
        }
        return nodeRepository.findById(nodeId)
            .orElseThrow(() -> new IllegalArgumentException(message));
    }

    private PhoneEntity resolvePhone(PhonePublicForm form) {
        NodeEntity node = nodeRepository.findById(form.getNodeId())
            .orElseThrow(() -> new IllegalArgumentException("Node not found."));
        return phoneRepository.findByNodeId(form.getNodeId())
            .orElseGet(() -> {
                PhonePatternEntity pattern = phonePatternRepository.findById(form.getPatternId())
                    .orElseThrow(() -> new IllegalArgumentException("Pattern not found."));
                PhoneEntity phone = new PhoneEntity();
                phone.setNode(node);
                phone.setPattern(pattern);
                return phoneRepository.save(phone);
            });
    }

    private void updateEdgeValue(EdgeEntity edge, String value, OffsetDateTime effectiveAt, String createdBy) {
        EdgeValueEntity current = edgeValueRepository.findCurrentValueByEdgeId(edge.getId(), effectiveAt)
            .orElse(null);
        if (current != null) {
            current.setExpiredAt(effectiveAt);
            edgeValueRepository.save(current);
        }
        EdgeValueEntity next = new EdgeValueEntity();
        next.setEdge(edge);
        next.setValue(value);
        next.setCreatedAt(effectiveAt);
        next.setCreatedBy(normalize(createdBy));
        edgeValueRepository.save(next);
    }

    private void updatePhoneValue(PhoneEntity phone, String value, OffsetDateTime effectiveAt, String createdBy) {
        PhoneValueEntity current = phoneValueRepository.findCurrentValueByPhoneId(phone.getId(), effectiveAt)
            .orElse(null);
        if (current != null) {
            current.setExpiredAt(effectiveAt);
            phoneValueRepository.save(current);
        }
        PhoneValueEntity next = new PhoneValueEntity();
        next.setPhone(phone);
        next.setValue(value);
        next.setCreatedAt(effectiveAt);
        next.setCreatedBy(normalize(createdBy));
        phoneValueRepository.save(next);
    }

    private void applyNodeValueUpdate(NodeValueForm form) {
        validateNodeValueForm(form);
        OffsetDateTime effectiveAt = form.getEffectiveAt() == null ? OffsetDateTime.now() : form.getEffectiveAt();
        NodeEntity node = nodeRepository.findById(form.getNodeId())
            .orElseThrow(() -> new IllegalArgumentException("Node not found."));
        NodeValueEntity current = nodeValueRepository.findCurrentValueByNodeId(node.getId(), effectiveAt)
            .orElse(null);
        if (current != null) {
            current.setExpiredAt(effectiveAt);
            nodeValueRepository.save(current);
        }
        NodeValueEntity next = new NodeValueEntity();
        next.setNode(node);
        next.setValue(form.getValue().trim());
        next.setCreatedAt(effectiveAt);
        next.setCreatedBy(normalize(form.getCreatedBy()));
        nodeValueRepository.save(next);
    }

    private void applyEdgeValueUpdate(EdgeValueForm form) {
        validateEdgeValueForm(form);
        OffsetDateTime effectiveAt = form.getEffectiveAt() == null ? OffsetDateTime.now() : form.getEffectiveAt();
        EdgeEntity edge = edgeRepository.findById(form.getEdgeId())
            .orElseThrow(() -> new IllegalArgumentException("Edge not found."));
        EdgeValueEntity current = edgeValueRepository.findCurrentValueByEdgeId(edge.getId(), effectiveAt)
            .orElse(null);
        if (current != null) {
            current.setExpiredAt(effectiveAt);
            edgeValueRepository.save(current);
        }
        EdgeValueEntity next = new EdgeValueEntity();
        next.setEdge(edge);
        next.setValue(form.getValue().trim());
        next.setCreatedAt(effectiveAt);
        next.setCreatedBy(normalize(form.getCreatedBy()));
        edgeValueRepository.save(next);
    }

    private void validateNodeForm(NodePublicForm form) {
        if (form == null || form.getValue() == null || form.getValue().isBlank()) {
            throw new IllegalArgumentException("Node value is required.");
        }
    }

    private void validateEdgeForm(EdgePublicForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Edge data is required.");
        }
        if (form.getFromNodeId() == null && form.getToNodeId() == null) {
            throw new IllegalArgumentException("Edge cannot be both PUBLIC and PRIVATE.");
        }
        if (form.getFromNodeId() != null && form.getToNodeId() != null
            && form.getFromNodeId().equals(form.getToNodeId())) {
            throw new IllegalArgumentException("Self-loops are not allowed.");
        }
        if (form.getCreatedAt() != null && form.getExpiredAt() != null
            && form.getCreatedAt().isAfter(form.getExpiredAt())) {
            throw new IllegalArgumentException("Created time must be before expired time.");
        }
    }

    private void validatePhoneForm(PhonePublicForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Phone data is required.");
        }
        if (form.getNodeId() == null) {
            throw new IllegalArgumentException("Node is required.");
        }
        if (form.getPatternId() == null) {
            throw new IllegalArgumentException("Pattern is required.");
        }
        if (form.getValue() == null || form.getValue().isBlank()) {
            throw new IllegalArgumentException("Value is required.");
        }
    }

    private void validateNodeValueForm(NodeValueForm form) {
        if (form.getNodeId() == null) {
            throw new IllegalArgumentException("Node is required.");
        }
        if (form.getValue() == null || form.getValue().isBlank()) {
            throw new IllegalArgumentException("Value is required.");
        }
    }

    private void validateEdgeValueForm(EdgeValueForm form) {
        if (form.getEdgeId() == null) {
            throw new IllegalArgumentException("Edge is required.");
        }
        if (form.getValue() == null || form.getValue().isBlank()) {
            throw new IllegalArgumentException("Value is required.");
        }
    }

    private Long parseLong(String value, String field) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(field + " must be a number.");
        }
    }

    private OffsetDateTime parseOffsetDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value);
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid datetime format.");
        }
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
