package com.example.graph.web;

import com.example.graph.web.form.EdgeValueForm;
import com.example.graph.web.form.NodeValueForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Public value patch request")
public class PublicValuesPatchRequest {
    private NodeValueForm nodeValue;
    private EdgeValueForm edgeValue;
}
