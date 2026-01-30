package com.example.graph.web.form;

import java.time.OffsetDateTime;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Node value payload")
public class NodeValueForm {
    @Schema(description = "Node id for value patch")
    private Long nodeId;
    @Schema(description = "Node display value")
    private String value;
    @Schema(description = "Optional HTML body")
    private String body;
    @Schema(description = "Effective timestamp")
    private OffsetDateTime effectiveAt;
}
