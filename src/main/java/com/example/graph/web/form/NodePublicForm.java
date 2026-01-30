package com.example.graph.web.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Node payload")
public class NodePublicForm {
    @Schema(description = "Node id (optional)")
    private Long id;
    @Schema(description = "Node value payload")
    private NodeValueForm value;
}
