package com.example.graph.service;

import com.example.graph.converter.EdgePublicConverter;
import com.example.graph.converter.GraphSnapshot;
import com.example.graph.converter.NodePublicConverter;
import com.example.graph.converter.UserPublicConverter;
import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.model.value.EdgeValueEntity;
import com.example.graph.model.value.NodeValueEntity;
import com.example.graph.model.user.UserEntity;
import com.example.graph.repository.EdgeRepository;
import com.example.graph.repository.EdgeValueRepository;
import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.NodeValueRepository;
import com.example.graph.repository.UserRepository;
import com.example.graph.service.user.ProfileService;
import com.example.graph.service.value.EdgeValueService;
import com.example.graph.service.value.NodeValueService;
import com.example.graph.validate.EdgePublicValidator;
import com.example.graph.validate.NodePublicValidator;
import com.example.graph.validate.UserPublicValidator;
import com.example.graph.validate.ValidationErrorCollector;
import com.example.graph.validate.ValidationException;
import com.example.graph.web.problem.ProblemFieldError;
import com.example.graph.web.PublicGraphPostRequest;
import com.example.graph.web.PublicValuesPatchRequest;
import com.example.graph.web.form.EdgePublicForm;
import com.example.graph.web.form.NodePublicForm;
import com.example.graph.web.form.UserPublicForm;
import com.example.graph.web.form.EdgeValueForm;
import com.example.graph.web.form.NodeValueForm;
import com.example.graph.snapshot.TimeSlice;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
    private final UserRepository userRepository;
    private final NodeValueService nodeValueService;
    private final EdgeValueService edgeValueService;
    private final ProfileService profileService;
    private final NodeValueRepository nodeValueRepository;
    private final EdgeValueRepository edgeValueRepository;
    private final NodePublicConverter nodePublicConverter;
    private final EdgePublicConverter edgePublicConverter;
    private final UserPublicConverter userPublicConverter;
    private final NodePublicValidator nodePublicValidator;
    private final EdgePublicValidator edgePublicValidator;
    private final UserPublicValidator userPublicValidator;

    public PublicGraphService(NodeRepository nodeRepository,
                              EdgeRepository edgeRepository,
                              UserRepository userRepository,
                              NodeValueService nodeValueService,
                              EdgeValueService edgeValueService,
                              ProfileService profileService,
                              NodeValueRepository nodeValueRepository,
                              EdgeValueRepository edgeValueRepository,
                              NodePublicConverter nodePublicConverter,
                              EdgePublicConverter edgePublicConverter,
                              UserPublicConverter userPublicConverter,
                              NodePublicValidator nodePublicValidator,
                              EdgePublicValidator edgePublicValidator,
                              UserPublicValidator userPublicValidator) {
        this.nodeRepository = nodeRepository;
        this.edgeRepository = edgeRepository;
        this.userRepository = userRepository;
        this.nodeValueService = nodeValueService;
        this.edgeValueService = edgeValueService;
        this.profileService = profileService;
        this.nodeValueRepository = nodeValueRepository;
        this.edgeValueRepository = edgeValueRepository;
        this.nodePublicConverter = nodePublicConverter;
        this.edgePublicConverter = edgePublicConverter;
        this.userPublicConverter = userPublicConverter;
        this.nodePublicValidator = nodePublicValidator;
        this.edgePublicValidator = edgePublicValidator;
        this.userPublicValidator = userPublicValidator;
    }

    @Transactional(readOnly = true)
    public GraphSnapshot loadGraph(Long nodeId, TimeSlice timeSlice) {
        OffsetDateTime resolved = timeSlice.getResolvedAt();
        List<EdgeEntity> edges = loadEdges(nodeId);
        List<NodeEntity> nodes = loadNodes(nodeId, edges);
        Set<Long> nodeIds = nodes.stream().map(NodeEntity::getId).collect(Collectors.toSet());
        List<UserEntity> users = loadUsers(nodeId, nodeIds);
        String scope = nodeId == null ? "FULL" : "1-hop";
        int hops = nodeId == null ? 0 : 1;
        return new GraphSnapshot(
            nodes,
            edges,
            users,
            nodeValueService.getCurrentValues(resolved),
            edgeValueService.getCurrentValueEntities(resolved),
            profileService.getCurrentProfiles(resolved),
            timeSlice,
            nodeId,
            scope,
            hops
        );
    }

    @Transactional
    public void applyGraph(PublicGraphPostRequest request, OffsetDateTime now) {
        List<NodePublicForm> nodes = request.getNodes() == null ? List.of() : request.getNodes();
        List<EdgePublicForm> edges = request.getEdges() == null ? List.of() : request.getEdges();
        List<UserPublicForm> users = request.getUsers() == null ? List.of() : request.getUsers();
        ValidationErrorCollector errors = new ValidationErrorCollector();
        for (int index = 0; index < nodes.size(); index++) {
            nodePublicValidator.validate(nodes.get(index), "nodes[" + index + "]", errors);
        }
        for (int index = 0; index < edges.size(); index++) {
            edgePublicValidator.validate(edges.get(index), "edges[" + index + "]", errors);
        }
        for (int index = 0; index < users.size(); index++) {
            userPublicValidator.validate(users.get(index), "users[" + index + "]", errors);
        }
        errors.throwIfAny();

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

        for (UserPublicForm form : users) {
            userPublicConverter.toEntity(form, now);
        }
    }

    @Transactional
    public void applyValuesPatch(PublicValuesPatchRequest request) {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        ValidationErrorCollector errors = new ValidationErrorCollector();
        if (request.getNodeValue() != null) {
            nodePublicValidator.validate(request.getNodeValue(), "nodeValue", errors);
        }
        if (request.getEdgeValue() != null) {
            edgePublicValidator.validate(request.getEdgeValue(), "edgeValue", errors);
        }
        errors.throwIfAny();
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
            .orElseThrow(() -> new ValidationException("Node not found.",
                List.of(new ProblemFieldError("nodeId", "Node not found."))));
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

    private List<UserEntity> loadUsers(Long nodeId, Set<Long> nodeIds) {
        if (nodeId == null) {
            return userRepository.findAll();
        }
        return userRepository.findAll().stream()
            .filter(user -> nodeIds.contains(user.getNode().getId()))
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

    private void applyNodeValueUpdate(NodeValueForm form, OffsetDateTime now) {
        OffsetDateTime effectiveAt = form.getEffectiveAt() == null ? now : form.getEffectiveAt();
        NodeEntity node = nodeRepository.findById(form.getNodeId())
            .orElseThrow(() -> new ValidationException("Node not found.",
                List.of(new ProblemFieldError("nodeValue.nodeId", "Node not found."))));
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
            .orElseThrow(() -> new ValidationException("Edge not found.",
                List.of(new ProblemFieldError("edgeValue.edgeId", "Edge not found."))));
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
