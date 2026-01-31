package com.example.graph.converter;

import com.example.graph.model.EdgeEntity;
import com.example.graph.model.NodeEntity;
import com.example.graph.model.user.UserEntity;
import com.example.graph.model.value.EdgeValueEntity;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JsonLdConverter {
    private static final List<String> META_INCLUDE = List.of("relations", "categories", "notes", "users");
    private static final Map<String, Object> CONTEXT = buildContext();

    public JsonLdDocument toJsonLd(GraphSnapshot snapshot) {
        List<Object> graph = new ArrayList<>();
        for (NodeEntity node : snapshot.getNodes()) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("@id", "node:" + node.getId());
            entry.put("@type", "Node");
            String value = snapshot.getNodeValues().get(node.getId());
            if (value != null) {
                entry.put("value", value);
            }
            graph.add(entry);
        }

        for (EdgeEntity edge : snapshot.getEdges()) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("@id", "edge:" + edge.getId());
            entry.put("@type", "Edge");
            entry.put("kind", resolveKind(edge));
            if (edge.getFromNode() != null) {
                entry.put("from", "node:" + edge.getFromNode().getId());
            }
            if (edge.getToNode() != null) {
                entry.put("to", "node:" + edge.getToNode().getId());
            }
            EdgeValueEntity currentValue = snapshot.getEdgeValues().get(edge.getId());
            if (currentValue != null && currentValue.getValue() != null) {
                entry.put("value", currentValue.getValue());
                if ("Relation".equals(entry.get("kind"))) {
                    entry.put("relationType", currentValue.getValue());
                }
            }
            if (currentValue != null && currentValue.getBody() != null) {
                entry.put("body", currentValue.getBody());
            }
            if (edge.getCreatedAt() != null) {
                entry.put("createdAt", edge.getCreatedAt());
            }
            if (edge.getExpiredAt() != null) {
                entry.put("expiredAt", edge.getExpiredAt());
            }
            graph.add(entry);
        }

        for (UserEntity user : snapshot.getUsers()) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("@id", "user:" + user.getId());
            entry.put("@type", "User");
            entry.put("node", "node:" + user.getNode().getId());
            graph.add(entry);
        }

        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("nodeId", snapshot.getFocusNodeId());
        meta.put("at", toIsoString(snapshot.getAtResolved()));
        meta.put("atRequested", toIsoString(snapshot.getAtRequested()));
        meta.put("atResolved", toIsoString(snapshot.getAtResolved()));
        meta.put("timezone", snapshot.getTimezone());
        meta.put("scope", snapshot.getScope());
        meta.put("hops", snapshot.getHops());
        meta.put("include", META_INCLUDE);
        meta.put("contextVersion", "v1");

        String id = buildSnapshotId(snapshot.getFocusNodeId(), snapshot.getAtResolved());
        return new JsonLdDocument(CONTEXT, id, "GraphSnapshot", meta, graph);
    }

    private String buildSnapshotId(Long nodeId, OffsetDateTime at) {
        String encodedAt = encodeIsoString(toIsoString(at));
        if (nodeId == null) {
            return "urn:shezhire:snapshot:full:" + encodedAt;
        }
        return "urn:shezhire:snapshot:node:" + nodeId + ":" + encodedAt;
    }

    private String toIsoString(OffsetDateTime at) {
        return at == null ? null : at.toString();
    }

    private static String encodeIsoString(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(":", "%3A").replace("+", "%2B");
    }

    private static Map<String, Object> buildContext() {
        Map<String, Object> context = new LinkedHashMap<>();
        context.put("@vocab", "https://example.org/shezhire#");
        context.put("Node", "Node");
        context.put("Edge", "Edge");
        context.put("User", "User");
        context.put("id", "@id");
        context.put("type", "@type");
        context.put("kind", "kind");
        context.put("relationType", "relationType");
        context.put("createdAt", "createdAt");
        context.put("expiredAt", "expiredAt");
        context.put("value", "value");
        context.put("body", "body");
        Map<String, Object> from = new LinkedHashMap<>();
        from.put("@id", "from");
        from.put("@type", "@id");
        context.put("from", from);
        Map<String, Object> to = new LinkedHashMap<>();
        to.put("@id", "to");
        to.put("@type", "@id");
        context.put("to", to);
        Map<String, Object> hasUser = new LinkedHashMap<>();
        hasUser.put("@id", "hasUser");
        hasUser.put("@type", "@id");
        context.put("hasUser", hasUser);
        return Collections.unmodifiableMap(context);
    }

    private String resolveKind(EdgeEntity edge) {
        if (edge.isCategory()) {
            return "Category";
        }
        if (edge.isNote()) {
            return "Note";
        }
        return "Relation";
    }
}
