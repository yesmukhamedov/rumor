package com.example.graph.controller;

import com.example.graph.service.NodeService;
import com.example.graph.web.NodeForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/nodes")
public class AdminNodeController {
    private final NodeService nodeService;

    public AdminNodeController(NodeService nodeService) {
        this.nodeService = nodeService;
    }

    @GetMapping
    public String nodes(Model model) {
        model.addAttribute("nodes", nodeService.listNodesDto());
        if (!model.containsAttribute("nodeForm")) {
            model.addAttribute("nodeForm", new NodeForm());
        }
        return "admin/nodes";
    }

    @PostMapping
    public String createNode(@Valid NodeForm nodeForm, BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("nodeForm", nodeForm);
            return "redirect:/admin/nodes";
        }
        nodeService.createNode(nodeForm.getName());
        redirectAttributes.addFlashAttribute("success", "Node created.");
        return "redirect:/admin/nodes";
    }

    @PostMapping("/{id}/delete")
    public String deleteNode(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        nodeService.deleteNode(id);
        redirectAttributes.addFlashAttribute("success", "Node deleted.");
        return "redirect:/admin/nodes";
    }
}
