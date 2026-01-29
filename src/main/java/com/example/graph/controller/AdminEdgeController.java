package com.example.graph.controller;

import com.example.graph.service.EdgeService;
import com.example.graph.service.NodeService;
import com.example.graph.web.EdgeForm;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/edges")
public class AdminEdgeController {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final String PUBLIC_SENTINEL = "PUBLIC";

    private final EdgeService edgeService;
    private final NodeService nodeService;

    public AdminEdgeController(EdgeService edgeService, NodeService nodeService) {
        this.edgeService = edgeService;
        this.nodeService = nodeService;
    }

    @GetMapping
    public String edges(Model model) {
        model.addAttribute("nodes", nodeService.listNodesDto());
        model.addAttribute("edges", edgeService.listEdgesDto());
        model.addAttribute("publicEdgeLabels", edgeService.getPublicEdgeLabels());
        if (!model.containsAttribute("edgeForm")) {
            model.addAttribute("edgeForm", new EdgeForm());
        }
        return "admin/edges";
    }

    @PostMapping
    public String createEdge(@Valid EdgeForm edgeForm, BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("edgeForm", edgeForm);
            return "redirect:/admin/edges";
        }
        String fromValue = edgeForm.getFromId();
        if (fromValue == null || fromValue.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "From is required (choose Public or a node).");
            redirectAttributes.addFlashAttribute("edgeForm", edgeForm);
            return "redirect:/admin/edges";
        }
        try {
            Long fromId = parseFromId(fromValue);
            LocalDateTime createdAt = parseDateTime(edgeForm.getCreatedAt());
            LocalDateTime expiredAt = parseDateTime(edgeForm.getExpiredAt());
            edgeService.createEdge(fromId, edgeForm.getToId(), edgeForm.getLabelId(), edgeForm.getNewLabel(),
                createdAt, expiredAt);
            redirectAttributes.addFlashAttribute("success", "Edge created.");
        } catch (NumberFormatException ex) {
            redirectAttributes.addFlashAttribute("error", "From node not found.");
            redirectAttributes.addFlashAttribute("edgeForm", edgeForm);
        } catch (DateTimeParseException ex) {
            redirectAttributes.addFlashAttribute("error", "Invalid date/time format.");
            redirectAttributes.addFlashAttribute("edgeForm", edgeForm);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "Edge violates graph constraints.");
        }
        return "redirect:/admin/edges";
    }

    @PostMapping("/{id}/delete")
    public String deleteEdge(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        edgeService.deleteEdge(id);
        redirectAttributes.addFlashAttribute("success", "Edge deleted.");
        return "redirect:/admin/edges";
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(value, DATE_TIME_FORMATTER);
    }

    private Long parseFromId(String fromId) {
        if (PUBLIC_SENTINEL.equals(fromId)) {
            return null;
        }
        return Long.parseLong(fromId);
    }
}
