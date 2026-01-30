package com.example.graph.web.problem;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Validation field error")
public class ProblemFieldError {
    @Schema(description = "Field path", example = "nodes[0].value.value")
    private String field;

    @Schema(description = "Error message", example = "Node value is required.")
    private String message;
}
