package com.example.graph.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class PhoneForm {
    @NotNull(message = "Pattern is required.")
    private Long patternId;

    @NotBlank(message = "Value is required.")
    @Size(max = 32, message = "Value must be at most 32 characters.")
    private String value;

    public Long getPatternId() {
        return patternId;
    }

    public void setPatternId(Long patternId) {
        this.patternId = patternId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
