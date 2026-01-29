package com.example.graph.web.dto;

public class PhoneDto {
    private final Long id;
    private final String nodeName;
    private final String patternCode;
    private final String value;

    public PhoneDto(Long id, String nodeName, String patternCode, String value) {
        this.id = id;
        this.nodeName = nodeName;
        this.patternCode = patternCode;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public String getNodeName() {
        return nodeName;
    }

    public String getPatternCode() {
        return patternCode;
    }

    public String getValue() {
        return value;
    }
}
