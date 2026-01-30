package com.example.graph.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicValuesPatchRequest {
    private NodeValueForm nodeValue;
    private EdgeValueForm edgeValue;
}
