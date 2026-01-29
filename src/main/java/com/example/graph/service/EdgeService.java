package com.example.graph.service;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.repository.EdgeRepository;
import com.example.graph.repository.NodeRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EdgeService {
    private final EdgeRepository edgeRepository;
    private final NodeRepository nodeRepository;

    public EdgeService(EdgeRepository edgeRepository, NodeRepository nodeRepository) {
        this.edgeRepository = edgeRepository;
        this.nodeRepository = nodeRepository;
    }

    public EdgeEntity createEdge(Long fromId, Long toId) {
        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("Both from and to nodes are required.");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Self-loops are not allowed.");
        }
        NodeEntity fromNode = nodeRepository.findById(fromId)
            .orElseThrow(() -> new IllegalArgumentException("From node not found."));
        NodeEntity toNode = nodeRepository.findById(toId)
            .orElseThrow(() -> new IllegalArgumentException("To node not found."));
        if (edgeRepository.existsByFromNodeIdAndToNodeId(fromId, toId)) {
            throw new IllegalArgumentException("That edge already exists.");
        }
        EdgeEntity edge = new EdgeEntity();
        edge.setFromNode(fromNode);
        edge.setToNode(toNode);
        return edgeRepository.save(edge);
    }

    public List<EdgeEntity> listEdges() {
        return edgeRepository.findAll();
    }

    public void deleteEdge(Long id) {
        edgeRepository.deleteById(id);
    }
}
