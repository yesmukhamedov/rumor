package com.example.graph.controller;

import com.example.graph.service.EdgeService;
import com.example.graph.service.NodeService;
import com.example.graph.web.EdgeDto;
import com.example.graph.web.NodeDto;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GraphViewController {
    private final NodeService nodeService;
    private final EdgeService edgeService;

    public GraphViewController(NodeService nodeService, EdgeService edgeService) {
        this.nodeService = nodeService;
        this.edgeService = edgeService;
    }

    @GetMapping("/graph/view")
    public String viewGraph(Model model) {
        List<NodeDto> nodes = nodeService.listNodes().stream()
            .map(node -> new NodeDto(node.getId(), node.getName()))
            .toList();
        List<EdgeDto> edges = edgeService.listEdges().stream()
            .map(edge -> new EdgeDto(edge.getFromNode().getId(), edge.getToNode().getId()))
            .toList();
        model.addAttribute("nodes", nodes);
        model.addAttribute("edges", edges);
        return "graph-view";
    }
}
