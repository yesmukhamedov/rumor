package com.example.graph.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhonePatternDto {
    private Long id;
    private String code;
    private String value;
}
