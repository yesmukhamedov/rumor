package com.example.graph.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JsonLdDocument {
    @JsonProperty("@context")
    private Object context;

    @JsonProperty("@id")
    private String id;

    @JsonProperty("@type")
    private Object type;

    private Map<String, Object> meta;

    @JsonProperty("@graph")
    private List<Object> graph;
}
