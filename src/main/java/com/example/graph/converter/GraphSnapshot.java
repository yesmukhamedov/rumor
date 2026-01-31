package com.example.graph.converter;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.model.user.ProfileEntity;
import com.example.graph.model.user.UserEntity;
import com.example.graph.model.value.EdgeValueEntity;
import com.example.graph.snapshot.TimeSlice;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class GraphSnapshot {
    private final List<NodeEntity> nodes;
    private final List<EdgeEntity> edges;
    private final List<UserEntity> users;
    private final Map<Long, String> nodeValues;
    private final Map<Long, EdgeValueEntity> edgeValues;
    private final Map<Long, ProfileEntity> profiles;
    private final TimeSlice timeSlice;
    private final Long focusNodeId;
    private final String scope;
    private final int hops;

    public GraphSnapshot(List<NodeEntity> nodes,
                         List<EdgeEntity> edges,
                         List<UserEntity> users,
                         Map<Long, String> nodeValues,
                         Map<Long, EdgeValueEntity> edgeValues,
                         Map<Long, ProfileEntity> profiles,
                         TimeSlice timeSlice,
                         Long focusNodeId,
                         String scope,
                         int hops) {
        this.nodes = nodes;
        this.edges = edges;
        this.users = users;
        this.nodeValues = nodeValues;
        this.edgeValues = edgeValues;
        this.profiles = profiles;
        this.timeSlice = timeSlice;
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

    public List<UserEntity> getUsers() {
        return users;
    }

    public Map<Long, String> getNodeValues() {
        return nodeValues;
    }

    public Map<Long, EdgeValueEntity> getEdgeValues() {
        return edgeValues;
    }

    public Map<Long, ProfileEntity> getProfiles() {
        return profiles;
    }

    public OffsetDateTime getAtRequested() {
        return timeSlice.getRequestedAt();
    }

    public OffsetDateTime getAtResolved() {
        return timeSlice.getResolvedAt();
    }

    public String getTimezone() {
        return timeSlice.getTimezone();
    }

    public TimeSlice getTimeSlice() {
        return timeSlice;
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
