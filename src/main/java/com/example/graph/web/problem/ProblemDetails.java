package com.example.graph.web.problem;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Problem details response")
public class ProblemDetails {
    @Schema(description = "Problem type URI", example = "https://example.org/problems/validation")
    private String type;

    @Schema(description = "Short title", example = "Validation error")
    private String title;

    @Schema(description = "HTTP status code", example = "400")
    private Integer status;

    @Schema(description = "Human readable detail", example = "Validation failed")
    private String detail;

    @Schema(description = "Request path", example = "/public/graph")
    private String instance;

    @Schema(description = "Field-level validation errors")
    private List<ProblemFieldError> errors;
}
