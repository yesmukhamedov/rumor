package com.example.graph.web.form;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User profile payload")
public class UserPublicForm {
    @Schema(description = "Node id for user")
    private Long nodeId;
}
