package com.example.graph.web.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Phone payload")
public class PhonePublicForm {
    @Schema(description = "Node id for phone")
    private Long nodeId;
    @Schema(description = "Pattern id for phone")
    private Long patternId;
    @Schema(description = "Digits-only phone value")
    private String value;
}
