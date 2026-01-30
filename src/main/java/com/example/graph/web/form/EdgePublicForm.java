package com.example.graph.web.form;

import java.time.OffsetDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Edge payload")
public class EdgePublicForm {
    @Schema(description = "Edge id (optional)")
    private Long id;
    @Schema(description = "From node id (null for public category)")
    private Long fromNodeId;
    @Schema(description = "To node id (null for private note)")
    private Long toNodeId;
    @Schema(description = "Edge value payload")
    private EdgeValueForm value;
    @Schema(description = "Edge created timestamp")
    private OffsetDateTime createdAt;
    @Schema(description = "Edge expired timestamp")
    private OffsetDateTime expiredAt;
}
