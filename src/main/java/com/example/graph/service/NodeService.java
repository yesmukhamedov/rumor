package com.example.graph.service;

import com.example.graph.model.NameEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.repository.NameRepository;
import com.example.graph.repository.NodeRepository;
import com.example.graph.web.dto.NodeDto;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NodeService {
    private final NodeRepository nodeRepository;
    private final NameRepository nameRepository;

    public NodeService(NodeRepository nodeRepository, NameRepository nameRepository) {
        this.nodeRepository = nodeRepository;
        this.nameRepository = nameRepository;
    }

    public NodeEntity createNode(String name) {
        NameEntity nameEntity = new NameEntity();
        nameEntity.setText(name);
        nameEntity.setCreatedAt(OffsetDateTime.now());
        NameEntity savedName = nameRepository.save(nameEntity);

        NodeEntity node = new NodeEntity();
        node.setName(savedName);
        return nodeRepository.save(node);
    }

    @Transactional(readOnly = true)
    public List<NodeDto> listNodesDto() {
        return nodeRepository.findAll().stream()
            .map(node -> new NodeDto(node.getId(), node.getName().getText()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<NodeDto> listNodesWithoutPhoneDto() {
        return nodeRepository.findNodesWithoutPhone().stream()
            .map(node -> new NodeDto(node.getId(), node.getName().getText()))
            .toList();
    }

    public void deleteNode(Long id) {
        nodeRepository.deleteById(id);
    }
}
