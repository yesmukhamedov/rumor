package com.example.graph.service;

import com.example.graph.model.NodeEntity;
import com.example.graph.repository.NodeRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NodeService {
    private final NodeRepository nodeRepository;

    public NodeService(NodeRepository nodeRepository) {
        this.nodeRepository = nodeRepository;
    }

    public NodeEntity createNode(String name) {
        NodeEntity node = new NodeEntity();
        node.setName(name);
        return nodeRepository.save(node);
    }

    public List<NodeEntity> listNodes() {
        return nodeRepository.findAll();
    }

    public void deleteNode(Long id) {
        nodeRepository.deleteById(id);
    }
}
