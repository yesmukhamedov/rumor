package com.example.graph.controller;

import com.example.graph.converter.GraphSnapshot;
import com.example.graph.converter.JsonLdConverter;
import com.example.graph.service.PublicGraphService;
import com.example.graph.validate.EdgePublicValidator;
import com.example.graph.validate.NodePublicValidator;
import com.example.graph.validate.PhonePublicValidator;
import com.example.graph.validate.ValidationException;
import com.example.graph.web.form.EdgePublicForm;
import com.example.graph.web.form.NodePublicForm;
import com.example.graph.web.form.PhonePublicForm;
import com.example.graph.web.PublicGraphPostRequest;
import com.example.graph.web.PublicValuesPatchRequest;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private final NodePublicValidator nodePublicValidator;
    private final EdgePublicValidator edgePublicValidator;
    private final PhonePublicValidator phonePublicValidator;
    private final JsonLdConverter jsonLdConverter;

    public PublicController(PublicGraphService publicGraphService,
                            NodePublicValidator nodePublicValidator,
                            EdgePublicValidator edgePublicValidator,
                            PhonePublicValidator phonePublicValidator,
                            JsonLdConverter jsonLdConverter) {
        this.publicGraphService = publicGraphService;
        this.nodePublicValidator = nodePublicValidator;
        this.edgePublicValidator = edgePublicValidator;
        this.phonePublicValidator = phonePublicValidator;
        this.jsonLdConverter = jsonLdConverter;
    }

    @GetMapping("/graph")
    public ResponseEntity<Map<String, Object>> getGraph(@RequestParam Map<String, String> params) {
        Long nodeId = parseLong(params.get("nodeId"), "nodeId");
        OffsetDateTime at = parseOffsetDateTime(params.get("at"));
        GraphSnapshot snapshot = publicGraphService.loadGraph(nodeId, at);
        return ResponseEntity.ok(jsonLdConverter.toJsonLd(snapshot));
    }

    @PostMapping(path = "/graph", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> postGraph(@RequestBody PublicGraphPostRequest request) {
        List<NodePublicForm> nodes = request.getNodes() == null ? List.of() : request.getNodes();
        List<EdgePublicForm> edges = request.getEdges() == null ? List.of() : request.getEdges();
        List<PhonePublicForm> phones = request.getPhones() == null ? List.of() : request.getPhones();
        OffsetDateTime now = OffsetDateTime.now();
        for (NodePublicForm form : nodes) {
            nodePublicValidator.validate(form);
        }
        for (EdgePublicForm form : edges) {
            edgePublicValidator.validate(form);
        }
        for (PhonePublicForm form : phones) {
            phonePublicValidator.validate(form);
        }
        publicGraphService.applyGraph(request, now);
        GraphSnapshot snapshot = publicGraphService.loadGraph(null, null);
        return ResponseEntity.ok(jsonLdConverter.toJsonLd(snapshot));
    }

    @PatchMapping(path = "/values", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> patchValues(@RequestBody PublicValuesPatchRequest request) {
        if (request.getNodeValue() != null) {
            nodePublicValidator.validate(request.getNodeValue());
        }
        if (request.getEdgeValue() != null) {
            edgePublicValidator.validate(request.getEdgeValue());
        }
        publicGraphService.applyValuesPatch(request);
        GraphSnapshot snapshot = publicGraphService.loadGraph(null, null);
        return ResponseEntity.ok(jsonLdConverter.toJsonLd(snapshot));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
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

}
