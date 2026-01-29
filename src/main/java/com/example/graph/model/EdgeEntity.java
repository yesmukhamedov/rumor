package com.example.graph.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
    name = "edges",
    uniqueConstraints = @UniqueConstraint(columnNames = {"from_id", "to_id"})
)
public class EdgeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "from_id", nullable = false)
    private NodeEntity fromNode;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "to_id", nullable = false)
    private NodeEntity toNode;

    public Long getId() {
        return id;
    }

    public NodeEntity getFromNode() {
        return fromNode;
    }

    public void setFromNode(NodeEntity fromNode) {
        this.fromNode = fromNode;
    }

    public NodeEntity getToNode() {
        return toNode;
    }

    public void setToNode(NodeEntity toNode) {
        this.toNode = toNode;
    }
}
