package com.example.graph.web.dto;

public class PhoneDto {
    private final Long id;
    private final String patternCode;
    private final String patternValue;
    private final String value;

    public PhoneDto(Long id, String patternCode, String patternValue, String value) {
        this.id = id;
        this.patternCode = patternCode;
        this.patternValue = patternValue;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public String getPatternCode() {
        return patternCode;
    }

    public String getPatternValue() {
        return patternValue;
    }

    public String getValue() {
        return value;
    }
}
