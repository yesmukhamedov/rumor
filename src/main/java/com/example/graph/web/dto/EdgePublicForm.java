package com.example.graph.web.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EdgePublicForm {
    private Long id;
    private Long fromNodeId;
    private Long toNodeId;
    private String value;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiredAt;
    private String createdBy;
}
