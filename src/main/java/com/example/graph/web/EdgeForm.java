package com.example.graph.web;

import jakarta.validation.constraints.NotNull;

public class EdgeForm {
    @NotNull(message = "From node is required.")
    private Long fromId;

    @NotNull(message = "To node is required.")
    private Long toId;

    public Long getFromId() {
        return fromId;
    }

    public void setFromId(Long fromId) {
        this.fromId = fromId;
    }

    public Long getToId() {
        return toId;
    }

    public void setToId(Long toId) {
        this.toId = toId;
    }
}
