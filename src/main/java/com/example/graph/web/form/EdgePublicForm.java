package com.example.graph.web.form;

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
    private EdgeValueForm value;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiredAt;
}
