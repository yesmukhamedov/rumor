package com.example.graph.web.dto;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EdgeDto {
    private Long id;
    private Long fromId;
    private Long toId;
    private String label;
    private OffsetDateTime createdAt;
    private OffsetDateTime expiredAt;
    private String fromName;
    private String toName;
    private boolean category;
    private boolean note;
    private boolean relation;
    private boolean invalid;
}
