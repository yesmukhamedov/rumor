package com.example.graph.service;

import com.example.graph.converter.EdgePublicConverter;
import com.example.graph.converter.GraphSnapshot;
import com.example.graph.converter.NodePublicConverter;
import com.example.graph.converter.PhonePublicConverter;
import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.model.phone.PhoneEntity;
import com.example.graph.model.value.EdgeValueEntity;
import com.example.graph.model.value.NodeValueEntity;
import com.example.graph.model.phone.PhoneValueEntity;
import com.example.graph.repository.EdgeRepository;
import com.example.graph.repository.EdgeValueRepository;
import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.NodeValueRepository;
import com.example.graph.repository.PhoneRepository;
import com.example.graph.repository.PhoneValueRepository;
import com.example.graph.service.phone.PhoneValueService;
import com.example.graph.service.value.EdgeValueService;
import com.example.graph.service.value.NodeValueService;
import com.example.graph.web.PublicGraphPostRequest;
import com.example.graph.web.PublicValuesPatchRequest;
import com.example.graph.web.form.EdgePublicForm;
import com.example.graph.web.form.NodePublicForm;
import com.example.graph.web.form.PhonePublicForm;
import com.example.graph.web.form.EdgeValueForm;
import com.example.graph.web.form.NodeValueForm;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
    private final NodeValueRepository nodeValueRepository;
    private final EdgeValueRepository edgeValueRepository;
    private final PhoneValueRepository phoneValueRepository;
    private final NodePublicConverter nodePublicConverter;
    private final EdgePublicConverter edgePublicConverter;
    private final PhonePublicConverter phonePublicConverter;

    public PublicGraphService(NodeRepository nodeRepository,
                              EdgeRepository edgeRepository,
                              PhoneRepository phoneRepository,
                              NodeValueService nodeValueService,
                              EdgeValueService edgeValueService,
                              PhoneValueService phoneValueService,
                              NodeValueRepository nodeValueRepository,
                              EdgeValueRepository edgeValueRepository,
                              PhoneValueRepository phoneValueRepository,
                              NodePublicConverter nodePublicConverter,
                              EdgePublicConverter edgePublicConverter,
                              PhonePublicConverter phonePublicConverter) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
        this.phoneRepository = phoneRepository;
        this.nodeValueService = nodeValueService;
        this.edgeValueService = edgeValueService;
        this.phoneValueService = phoneValueService;
        this.nodeValueRepository = nodeValueRepository;
        this.edgeValueRepository = edgeValueRepository;
        this.phoneValueRepository = phoneValueRepository;
        this.nodePublicConverter = nodePublicConverter;
        this.edgePublicConverter = edgePublicConverter;
        this.phonePublicConverter = phonePublicConverter;
    }

    @Transactional(readOnly = true)
    public GraphSnapshot loadGraph(Long nodeId, OffsetDateTime at) {
        OffsetDateTime now = at == null ? OffsetDateTime.now() : at;
        List<EdgeEntity> edges = loadEdges(nodeId);
        List<NodeEntity> nodes = loadNodes(nodeId, edges);
        Set<Long> nodeIds = nodes.stream().map(NodeEntity::getId).collect(Collectors.toSet());
        List<PhoneEntity> phones = loadPhones(nodeId, nodeIds);
        return new GraphSnapshot(
            nodes,
            edges,
            phones,
            nodeValueService.getCurrentValues(now),
            edgeValueService.getCurrentValues(now),
            phoneValueService.getCurrentValues(now),
            now
        );
    }

    @Transactional
    public void applyGraph(PublicGraphPostRequest request, OffsetDateTime now) {
        List<NodePublicForm> nodes = request.getNodes() == null ? List.of() : request.getNodes();
        List<EdgePublicForm> edges = request.getEdges() == null ? List.of() : request.getEdges();
        List<PhonePublicForm> phones = request.getPhones() == null ? List.of() : request.getPhones();

        for (NodePublicForm form : nodes) {
            NodeEntity node = nodePublicConverter.toEntity(form);
            if (form.getValue() != null) {
                NodeValueEntity value = nodePublicConverter.toValueEntity(node, form.getValue(), now);
                nodeValueRepository.save(value);
            }
        }

        for (EdgePublicForm form : edges) {
            EdgeEntity edge = edgePublicConverter.toEntity(form);
            if (form.getValue() != null) {
                EdgeValueEntity value = edgePublicConverter.toValueEntity(edge, form.getValue(), now);
                OffsetDateTime effectiveAt = value.getCreatedAt() == null ? now : value.getCreatedAt();
                updateEdgeValue(edge, value, effectiveAt);
            }
        }

        for (PhonePublicForm form : phones) {
            PhoneEntity phone = phonePublicConverter.toEntity(form);
            updatePhoneValue(phone, phonePublicConverter.toValueEntity(phone, form, now), now);
        }
    }

    @Transactional
    public void applyValuesPatch(PublicValuesPatchRequest request) {
        OffsetDateTime now = OffsetDateTime.now();
        if (request.getNodeValue() != null) {
            applyNodeValueUpdate(request.getNodeValue(), now);
        }
        if (request.getEdgeValue() != null) {
            applyEdgeValueUpdate(request.getEdgeValue(), now);
        }
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

    private void updateEdgeValue(EdgeEntity edge, EdgeValueEntity next, OffsetDateTime effectiveAt) {
        EdgeValueEntity current = edgeValueRepository.findCurrentValueByEdgeId(edge.getId(), effectiveAt)
            .orElse(null);
        if (current != null) {
            current.setExpiredAt(effectiveAt);
            edgeValueRepository.save(current);
        }
        edgeValueRepository.save(next);
    }

    private void updatePhoneValue(PhoneEntity phone, PhoneValueEntity next, OffsetDateTime effectiveAt) {
        PhoneValueEntity current = phoneValueRepository.findCurrentValueByPhoneId(phone.getId(), effectiveAt)
            .orElse(null);
        if (current != null) {
            current.setExpiredAt(effectiveAt);
            phoneValueRepository.save(current);
        }
        phoneValueRepository.save(next);
    }

    private void applyNodeValueUpdate(NodeValueForm form, OffsetDateTime now) {
        OffsetDateTime effectiveAt = form.getEffectiveAt() == null ? now : form.getEffectiveAt();
        NodeEntity node = nodeRepository.findById(form.getNodeId())
            .orElseThrow(() -> new IllegalArgumentException("Node not found."));
        NodeValueEntity current = nodeValueRepository.findCurrentValueByNodeId(node.getId(), effectiveAt)
            .orElse(null);
        if (current != null) {
            current.setExpiredAt(effectiveAt);
            nodeValueRepository.save(current);
        }
        NodeValueEntity next = nodePublicConverter.toValueEntity(node, form, effectiveAt);
        nodeValueRepository.save(next);
    }

    private void applyEdgeValueUpdate(EdgeValueForm form, OffsetDateTime now) {
        OffsetDateTime effectiveAt = form.getEffectiveAt() == null ? now : form.getEffectiveAt();
        EdgeEntity edge = edgeRepository.findById(form.getEdgeId())
            .orElseThrow(() -> new IllegalArgumentException("Edge not found."));
        EdgeValueEntity current = edgeValueRepository.findCurrentValueByEdgeId(edge.getId(), effectiveAt)
            .orElse(null);
        if (current != null) {
            current.setExpiredAt(effectiveAt);
            edgeValueRepository.save(current);
        }
        EdgeValueEntity next = edgePublicConverter.toValueEntity(edge, form, effectiveAt);
        edgeValueRepository.save(next);
    }
}
