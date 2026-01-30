package com.example.graph.converter;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.model.phone.PhoneEntity;
import com.example.graph.model.phone.PhoneValueEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class GraphSnapshot {
    private final List<NodeEntity> nodes;
    private final List<EdgeEntity> edges;
    private final List<PhoneEntity> phones;
    private final Map<Long, String> nodeValues;
    private final Map<Long, String> edgeValues;
    private final Map<Long, PhoneValueEntity> phoneValues;
    private final OffsetDateTime at;
    private final Long focusNodeId;
    private final String scope;

    public GraphSnapshot(List<NodeEntity> nodes,
                         List<EdgeEntity> edges,
                         List<PhoneEntity> phones,
                         Map<Long, String> nodeValues,
                         Map<Long, String> edgeValues,
                         Map<Long, PhoneValueEntity> phoneValues,
                         OffsetDateTime at,
                         Long focusNodeId,
                         String scope) {
        this.nodes = nodes;
        this.edges = edges;
        this.phones = phones;
        this.nodeValues = nodeValues;
        this.edgeValues = edgeValues;
        this.phoneValues = phoneValues;
        this.at = at;
        this.focusNodeId = focusNodeId;
        this.scope = scope;
    }

    public List<NodeEntity> getNodes() {
        return nodes;
    }

    public List<EdgeEntity> getEdges() {
        return edges;
    }

    public List<PhoneEntity> getPhones() {
        return phones;
    }

    public Map<Long, String> getNodeValues() {
        return nodeValues;
    }

    public Map<Long, String> getEdgeValues() {
        return edgeValues;
    }

    public Map<Long, PhoneValueEntity> getPhoneValues() {
        return phoneValues;
    }

    public OffsetDateTime getAt() {
        return at;
    }

    public OffsetDateTime getAtTime() {
        return at;
    }

    public Long getFocusNodeId() {
        return focusNodeId;
    }

    public String getScope() {
        return scope;
    }
}
