package com.example.graph.web;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserForm {
    @NotNull(message = "Node is required.")
    private Long nodeId;
}
