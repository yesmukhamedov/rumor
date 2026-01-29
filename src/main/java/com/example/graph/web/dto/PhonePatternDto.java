package com.example.graph.web.dto;

public class PhonePatternDto {
    private final Long id;
    private final String code;
    private final String value;

    public PhonePatternDto(Long id, String code, String value) {
        this.id = id;
        this.code = code;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
