package com.example.graph.web;

import java.util.List;

import com.example.graph.web.form.EdgePublicForm;
import com.example.graph.web.form.NodePublicForm;
import com.example.graph.web.form.PhonePublicForm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Public graph bulk create/update request")
public class PublicGraphPostRequest {
    private List<NodePublicForm> nodes;
    private List<EdgePublicForm> edges;
    private List<PhonePublicForm> phones;
}
