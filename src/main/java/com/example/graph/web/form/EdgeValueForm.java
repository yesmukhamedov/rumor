package com.example.graph.web.form;

import java.time.OffsetDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Edge value payload")
public class EdgeValueForm {
    @Schema(description = "Edge id for value patch")
    private Long edgeId;
    @Schema(description = "Edge display value")
    private String value;
    @Schema(description = "Relation type for relation edges")
    private String relationType;
    @Schema(description = "Optional HTML body")
    private String body;
    @Schema(description = "Effective timestamp")
    private OffsetDateTime effectiveAt;
}
