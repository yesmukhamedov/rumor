package com.example.graph.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodePublicForm {
    private Long id;
    private String value;
    private String createdBy;
}
