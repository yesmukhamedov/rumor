package com.example.graph.controller;

import com.example.graph.service.EdgeService;
import com.example.graph.service.NodeService;
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
        return "redirect:/admin/nodes";
    }

    @GetMapping("/admin")
    public String adminHome() {
        return "redirect:/admin/nodes";
    }

    @GetMapping("/graph")
    public String graph() {
        return "redirect:/admin/nodes";
    }

    @GetMapping("/graph/view")
    public String viewGraph(Model model) {
        model.addAttribute("nodes", nodeService.listNodesDto());
        model.addAttribute("edges", edgeService.listEdgesDto().stream()
            .filter(edge -> edge.getFromId() != null)
            .toList());
        model.addAttribute("publicEdges", edgeService.getPublicEdges());
        return "graph-view";
    }
}
