package com.example.graph.web.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeValueForm {
    private Long nodeId;
    private String value;
    private OffsetDateTime effectiveAt;
    private String createdBy;
}
