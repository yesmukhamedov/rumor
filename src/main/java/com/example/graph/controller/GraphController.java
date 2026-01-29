package com.example.graph.controller;

import com.example.graph.service.EdgeService;
import com.example.graph.service.NodeService;
import com.example.graph.web.EdgeForm;
import com.example.graph.web.NodeForm;
import com.example.graph.web.dto.EdgeDto;
import com.example.graph.web.dto.NodeDto;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class GraphController {
    private final NodeService nodeService;
    private final EdgeService edgeService;

    public GraphController(NodeService nodeService, EdgeService edgeService) {
        this.nodeService = nodeService;
        this.edgeService = edgeService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/graph";
    }

    @GetMapping("/graph")
    public String graph(Model model) {
        model.addAttribute("nodes", nodeService.listNodes());
        model.addAttribute("edges", edgeService.listEdges());
        if (!model.containsAttribute("nodeForm")) {
            model.addAttribute("nodeForm", new NodeForm());
        }
        if (!model.containsAttribute("edgeForm")) {
            model.addAttribute("edgeForm", new EdgeForm());
        }
        return "graph";
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
