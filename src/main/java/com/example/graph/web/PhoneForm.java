package com.example.graph.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PhoneForm {
    @NotNull(message = "Node is required.")
    private Long nodeId;

    @NotNull(message = "Pattern is required.")
    private Long patternId;

    @NotBlank(message = "Value is required.")
    @Size(max = 64, message = "Value must be at most 64 characters.")
    private String value;

    public Long getPatternId() {
        return patternId;
    }

    public void setPatternId(Long patternId) {
        this.patternId = patternId;
    }

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
