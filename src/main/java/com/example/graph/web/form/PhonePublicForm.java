package com.example.graph.web.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhonePublicForm {
    private Long nodeId;
    private Long patternId;
    private String value;
}
