package com.example.graph.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(
    name = "edges",
    uniqueConstraints = @UniqueConstraint(columnNames = {"from_id", "to_id"})
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"fromNode", "toNode"})
public class EdgeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "from_id", nullable = true)
    private NodeEntity fromNode;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "to_id", nullable = true)
    private NodeEntity toNode;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "expired_at")
    private OffsetDateTime expiredAt;

    public boolean isCategory() {
        return fromNode == null && toNode != null;
    }

    public boolean isNote() {
        return fromNode != null && toNode == null;
    }

    public boolean isRelation() {
        return fromNode != null && toNode != null;
    }

    public boolean isInvalid() {
        return fromNode == null && toNode == null;
    }
}
