package com.example.graph.service;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.model.PhoneEntity;
import com.example.graph.repository.EdgeRepository;
import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.PhoneRepository;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PublicGraphService {
    private final NodeRepository nodeRepository;
    private final EdgeRepository edgeRepository;
    private final PhoneRepository phoneRepository;
    private final NodeValueService nodeValueService;
    private final EdgeValueService edgeValueService;
    private final PhoneValueService phoneValueService;

    public PublicGraphService(NodeRepository nodeRepository,
                              EdgeRepository edgeRepository,
                              PhoneRepository phoneRepository,
                              NodeValueService nodeValueService,
                              EdgeValueService edgeValueService,
                              PhoneValueService phoneValueService) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
        this.phoneRepository = phoneRepository;
        this.nodeValueService = nodeValueService;
        this.edgeValueService = edgeValueService;
        this.phoneValueService = phoneValueService;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> buildGraph(Long nodeId, OffsetDateTime at) {
        OffsetDateTime now = at == null ? OffsetDateTime.now() : at;
        List<EdgeEntity> edges = loadEdges(nodeId);
        List<NodeEntity> nodes = loadNodes(nodeId, edges);
        Set<Long> nodeIds = nodes.stream().map(NodeEntity::getId).collect(Collectors.toSet());
        List<PhoneEntity> phones = loadPhones(nodeId, nodeIds);

        Map<Long, String> nodeValues = nodeValueService.getCurrentValues(now);
        Map<Long, String> edgeValues = edgeValueService.getCurrentValues(now);
        Map<Long, String> phoneValues = phoneValueService.getCurrentValues(now);

        List<Map<String, Object>> graph = new ArrayList<>();
        for (NodeEntity node : nodes) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("@id", "node:" + node.getId());
            entry.put("@type", "Node");
            String value = nodeValues.get(node.getId());
            if (value != null) {
                entry.put("value", value);
            }
            graph.add(entry);
        }

        for (EdgeEntity edge : edges) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("@id", "edge:" + edge.getId());
            entry.put("@type", "Edge");
            entry.put("kind", resolveKind(edge));
            if (edge.getFromNode() != null) {
                entry.put("from", "node:" + edge.getFromNode().getId());
            }
            if (edge.getToNode() != null) {
                entry.put("to", "node:" + edge.getToNode().getId());
            }
            String value = edgeValues.get(edge.getId());
            if (value != null) {
                entry.put("value", value);
            }
            if (edge.getCreatedAt() != null) {
                entry.put("createdAt", edge.getCreatedAt());
            }
            if (edge.getExpiredAt() != null) {
                entry.put("expiredAt", edge.getExpiredAt());
            }
            graph.add(entry);
        }

        for (PhoneEntity phone : phones) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("@id", "phone:" + phone.getId());
            entry.put("@type", "Phone");
            entry.put("node", "node:" + phone.getNode().getId());
            entry.put("pattern", phone.getPattern().getCode());
            String value = phoneValues.get(phone.getId());
            if (value != null) {
                entry.put("value", value);
            }
            graph.add(entry);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("@context", buildContext());
        response.put("@graph", graph);
        return response;
    }

    private List<EdgeEntity> loadEdges(Long nodeId) {
        if (nodeId == null) {
            return edgeRepository.findAll();
        }
        List<EdgeEntity> edges = new ArrayList<>(edgeRepository.findAllByFromNodeId(nodeId));
        edgeRepository.findAllByToNodeId(nodeId).stream()
            .filter(edge -> edges.stream().noneMatch(existing -> existing.getId().equals(edge.getId())))
            .forEach(edges::add);
        return edges;
    }

    private List<NodeEntity> loadNodes(Long nodeId, List<EdgeEntity> edges) {
        if (nodeId == null) {
            return nodeRepository.findAll();
        }
        NodeEntity root = nodeRepository.findById(nodeId)
            .orElseThrow(() -> new IllegalArgumentException("Node not found."));
        Set<Long> ids = new HashSet<>();
        ids.add(root.getId());
        for (EdgeEntity edge : edges) {
            if (edge.getFromNode() != null) {
                ids.add(edge.getFromNode().getId());
            }
            if (edge.getToNode() != null) {
                ids.add(edge.getToNode().getId());
            }
        }
        return nodeRepository.findAllById(ids);
    }

    private List<PhoneEntity> loadPhones(Long nodeId, Set<Long> nodeIds) {
        if (nodeId == null) {
            return phoneRepository.findAll();
        }
        return phoneRepository.findAll().stream()
            .filter(phone -> nodeIds.contains(phone.getNode().getId()))
            .toList();
    }

    private Map<String, Object> buildContext() {
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("@vocab", "https://example.org/shezhire#");
        context.put("Node", "Node");
        context.put("Edge", "Edge");
        context.put("Phone", "Phone");
        context.put("id", "@id");
        context.put("type", "@type");
        Map<String, Object> from = new LinkedHashMap<>();
        from.put("@id", "from");
        from.put("@type", "@id");
        context.put("from", from);
        Map<String, Object> to = new LinkedHashMap<>();
        to.put("@id", "to");
        to.put("@type", "@id");
        context.put("to", to);
        Map<String, Object> hasPhone = new LinkedHashMap<>();
        hasPhone.put("@id", "hasPhone");
        hasPhone.put("@type", "@id");
        context.put("hasPhone", hasPhone);
        return context;
    }

    private String resolveKind(EdgeEntity edge) {
        if (edge.isCategory()) {
            return "Category";
        }
        if (edge.isNote()) {
            return "Note";
        }
        return "Relation";
    }
}
