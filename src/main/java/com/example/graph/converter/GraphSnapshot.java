package com.example.graph.converter;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.model.phone.PhoneEntity;
import com.example.graph.model.phone.PhoneValueEntity;
import com.example.graph.model.value.EdgeValueEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class GraphSnapshot {
    private final List<NodeEntity> nodes;
    private final List<EdgeEntity> edges;
    private final List<PhoneEntity> phones;
    private final Map<Long, String> nodeValues;
    private final Map<Long, EdgeValueEntity> edgeValues;
    private final Map<Long, PhoneValueEntity> phoneValues;
    private final String atRequested;
    private final OffsetDateTime atResolved;
    private final String timezone;
    private final Long focusNodeId;
    private final String scope;
    private final int hops;

    public GraphSnapshot(List<NodeEntity> nodes,
                         List<EdgeEntity> edges,
                         List<PhoneEntity> phones,
                         Map<Long, String> nodeValues,
                         Map<Long, EdgeValueEntity> edgeValues,
                         Map<Long, PhoneValueEntity> phoneValues,
                         String atRequested,
                         OffsetDateTime atResolved,
                         String timezone,
                         Long focusNodeId,
                         String scope,
                         int hops) {
        this.nodes = nodes;
        this.edges = edges;
        this.phones = phones;
        this.nodeValues = nodeValues;
        this.edgeValues = edgeValues;
        this.phoneValues = phoneValues;
        this.atRequested = atRequested;
        this.atResolved = atResolved;
        this.timezone = timezone;
        this.focusNodeId = focusNodeId;
        this.scope = scope;
        this.hops = hops;
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

    public Map<Long, EdgeValueEntity> getEdgeValues() {
        return edgeValues;
    }

    public Map<Long, PhoneValueEntity> getPhoneValues() {
        return phoneValues;
    }

    public String getAtRequested() {
        return atRequested;
    }

    public OffsetDateTime getAtResolved() {
        return atResolved;
    }

    public String getTimezone() {
        return timezone;
    }

    public Long getFocusNodeId() {
        return focusNodeId;
    }

    public String getScope() {
        return scope;
    }

    public int getHops() {
        return hops;
    }
}
