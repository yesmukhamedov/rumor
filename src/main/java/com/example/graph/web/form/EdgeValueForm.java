package com.example.graph.web.form;

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
    private String body;
    private OffsetDateTime effectiveAt;
}
