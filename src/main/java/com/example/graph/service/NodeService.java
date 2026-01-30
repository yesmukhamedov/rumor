package com.example.graph.service;

import com.example.graph.model.NodeEntity;
import com.example.graph.model.NodeValueEntity;
import com.example.graph.repository.NodeRepository;
import com.example.graph.web.dto.NodeDto;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NodeService {
    private final NodeRepository nodeRepository;
    private final NodeValueService nodeValueService;

    public NodeService(NodeRepository nodeRepository, NodeValueService nodeValueService) {
        this.nodeRepository = nodeRepository;
        this.nodeValueService = nodeValueService;
    }

    public NodeEntity createNode(String name) {
        OffsetDateTime now = OffsetDateTime.now();
        NodeEntity node = new NodeEntity();
        NodeEntity savedNode = nodeRepository.save(node);

        NodeValueEntity nodeValue = new NodeValueEntity();
        nodeValue.setNode(savedNode);
        nodeValue.setValue(name);
        nodeValue.setCreatedAt(now);
        nodeValueService.createCurrentValue(nodeValue);
        return savedNode;
    }

    @Transactional(readOnly = true)
    public List<NodeDto> listNodesDto() {
        OffsetDateTime now = OffsetDateTime.now();
        Map<Long, String> currentValues = nodeValueService.getCurrentValues(now);
        return nodeRepository.findAll().stream()
            .map(node -> new NodeDto(node.getId(), currentValues.getOrDefault(node.getId(), "—")))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<NodeDto> listNodesWithoutPhoneDto() {
        OffsetDateTime now = OffsetDateTime.now();
        Map<Long, String> currentValues = nodeValueService.getCurrentValues(now);
        return nodeRepository.findNodesWithoutPhone().stream()
            .map(node -> new NodeDto(node.getId(), currentValues.getOrDefault(node.getId(), "—")))
            .toList();
    }

    public void deleteNode(Long id) {
        nodeRepository.deleteById(id);
    }
}
