package com.example.graph.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NodeForm {
    @NotBlank(message = "Name is required.")
    @Size(max = 100, message = "Name must be at most 100 characters.")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
