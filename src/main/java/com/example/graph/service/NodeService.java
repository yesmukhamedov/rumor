package com.example.graph.service;

import com.example.graph.model.NodeEntity;
import com.example.graph.model.ValueEntity;
import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.ValueRepository;
import com.example.graph.web.dto.NodeDto;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NodeService {
    private final NodeRepository nodeRepository;
    private final ValueRepository valueRepository;

    public NodeService(NodeRepository nodeRepository, ValueRepository valueRepository) {
        this.nodeRepository = nodeRepository;
        this.valueRepository = valueRepository;
    }

    public NodeEntity createNode(String name) {
        ValueEntity valueEntity = new ValueEntity();
        valueEntity.setText(name);
        valueEntity.setCreatedAt(OffsetDateTime.now());
        ValueEntity savedValue = valueRepository.save(valueEntity);

        NodeEntity node = new NodeEntity();
        node.setValue(savedValue);
        return nodeRepository.save(node);
    }

    @Transactional(readOnly = true)
    public List<NodeDto> listNodesDto() {
        return nodeRepository.findAll().stream()
            .map(node -> new NodeDto(node.getId(), node.getValue().getText()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<NodeDto> listNodesWithoutPhoneDto() {
        return nodeRepository.findNodesWithoutPhone().stream()
            .map(node -> new NodeDto(node.getId(), node.getValue().getText()))
            .toList();
    }

    public void deleteNode(Long id) {
        nodeRepository.deleteById(id);
    }
}
