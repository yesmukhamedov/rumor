package com.example.graph.web.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicGraphPostRequest {
    private List<NodePublicForm> nodes;
    private List<EdgePublicForm> edges;
    private List<PhonePublicForm> phones;
}
