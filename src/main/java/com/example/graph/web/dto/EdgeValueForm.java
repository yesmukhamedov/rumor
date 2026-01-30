package com.example.graph.web.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EdgeValueForm {
    private Long edgeId;
    private String value;
    private OffsetDateTime effectiveAt;
    private String createdBy;
}
