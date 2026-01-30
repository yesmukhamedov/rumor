package com.example.graph.converter;

import com.example.graph.model.NodeEntity;
import com.example.graph.model.value.NodeValueEntity;
import com.example.graph.repository.NodeRepository;
import com.example.graph.util.HtmlSanitizer;
import com.example.graph.web.form.NodePublicForm;
import com.example.graph.web.form.NodeValueForm;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;

@Component
public class NodePublicConverter {
    private final NodeRepository nodeRepository;
    private final HtmlSanitizer htmlSanitizer;

    public NodePublicConverter(NodeRepository nodeRepository, HtmlSanitizer htmlSanitizer) {
        this.nodeRepository = nodeRepository;
        this.htmlSanitizer = htmlSanitizer;
    }

    public NodeEntity toEntity(NodePublicForm form) {
        NodeEntity node = null;
        if (form.getId() != null) {
            node = nodeRepository.findById(form.getId()).orElse(null);
        }
        if (node == null) {
            node = new NodeEntity();
        }
        return nodeRepository.save(node);
    }

    public NodeValueEntity toValueEntity(NodeEntity node, NodeValueForm form, OffsetDateTime now) {
        NodeValueEntity value = new NodeValueEntity();
        value.setNode(node);
        value.setValue(form.getValue().trim());
        value.setBody(htmlSanitizer.sanitize(form.getBody()));
        value.setCreatedAt(resolveEffectiveAt(form.getEffectiveAt(), now));
        return value;
    }

    private OffsetDateTime resolveEffectiveAt(OffsetDateTime effectiveAt, OffsetDateTime fallback) {
        return effectiveAt == null ? fallback : effectiveAt;
    }

}
