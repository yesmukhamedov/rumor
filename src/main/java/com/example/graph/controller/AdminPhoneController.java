package com.example.graph.controller;

import com.example.graph.service.PhoneService;
import com.example.graph.web.PhoneForm;
import jakarta.validation.Valid;
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
@RequestMapping("/admin/phones")
public class AdminPhoneController {
    private final PhoneService phoneService;

    public AdminPhoneController(PhoneService phoneService) {
        this.phoneService = phoneService;
    }

    @GetMapping
    public String phones(Model model) {
        model.addAttribute("phones", phoneService.listPhonesDto());
        model.addAttribute("patterns", phoneService.listPatternsDto());
        if (!model.containsAttribute("phoneForm")) {
            model.addAttribute("phoneForm", new PhoneForm());
        }
        return "admin/phones";
    }

    @PostMapping
    public String createPhone(@Valid PhoneForm phoneForm, BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("phoneForm", phoneForm);
            return "redirect:/admin/phones";
        }
        try {
            phoneService.createPhone(phoneForm.getPatternId(), phoneForm.getValue());
            redirectAttributes.addFlashAttribute("success", "Phone created.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("phoneForm", phoneForm);
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "Phone violates constraints.");
            redirectAttributes.addFlashAttribute("phoneForm", phoneForm);
        }
        return "redirect:/admin/phones";
    }

    @PostMapping("/{id}/delete")
    public String deletePhone(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        phoneService.deletePhone(id);
        redirectAttributes.addFlashAttribute("success", "Phone deleted.");
        return "redirect:/admin/phones";
    }
}
