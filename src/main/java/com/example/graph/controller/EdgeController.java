package com.example.graph.controller;

import com.example.graph.service.EdgeService;
import com.example.graph.web.EdgeForm;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/edges")
public class EdgeController {
    private final EdgeService edgeService;

    public EdgeController(EdgeService edgeService) {
        this.edgeService = edgeService;
    }

    @PostMapping
    public String createEdge(@Valid EdgeForm edgeForm, BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("edgeForm", edgeForm);
            return "redirect:/graph";
        }
        try {
            edgeService.createEdge(edgeForm.getFromId(), edgeForm.getToId());
            redirectAttributes.addFlashAttribute("success", "Edge created.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "Edge violates graph constraints.");
        }
        return "redirect:/graph";
    }

    @PostMapping("/{id}/delete")
    public String deleteEdge(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        edgeService.deleteEdge(id);
        redirectAttributes.addFlashAttribute("success", "Edge deleted.");
        return "redirect:/graph";
    }
}
