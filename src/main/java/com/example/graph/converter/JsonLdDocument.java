package com.example.graph.converter;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "JSON-LD document wrapper")
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
