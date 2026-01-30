package com.example.graph.web.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodePublicForm {
    private Long id;
    private NodeValueForm value;
}
