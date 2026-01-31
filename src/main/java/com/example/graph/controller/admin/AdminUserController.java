package com.example.graph.controller.admin;

import com.example.graph.service.NodeService;
import com.example.graph.service.user.UserService;
import com.example.graph.validate.ValidationException;
import com.example.graph.web.UserForm;
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
@RequestMapping("/admin/users")
public class AdminUserController {
    private final UserService userService;
    private final NodeService nodeService;

    public AdminUserController(UserService userService, NodeService nodeService) {
        this.userService = userService;
        this.nodeService = nodeService;
    }

    @GetMapping
    public String users(Model model) {
        model.addAttribute("users", userService.listUsersDto());
        model.addAttribute("availableNodes", nodeService.listNodesWithoutUserDto());
        if (!model.containsAttribute("userForm")) {
            model.addAttribute("userForm", new UserForm());
        }
        return "admin/users";
    }

    @PostMapping
    public String createUser(@Valid UserForm userForm, BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getAllErrors().get(0).getDefaultMessage());
            redirectAttributes.addFlashAttribute("userForm", userForm);
            return "redirect:/admin/users";
        }
        try {
            userService.createUserForNode(userForm.getNodeId());
            redirectAttributes.addFlashAttribute("success", "User created.");
        } catch (IllegalArgumentException | ValidationException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            redirectAttributes.addFlashAttribute("userForm", userForm);
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("error", "User violates constraints.");
            redirectAttributes.addFlashAttribute("userForm", userForm);
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("success", "User deleted.");
        return "redirect:/admin/users";
    }
}
