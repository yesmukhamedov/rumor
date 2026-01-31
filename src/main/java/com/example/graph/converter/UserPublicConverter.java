package com.example.graph.converter;

import com.example.graph.model.NodeEntity;
import com.example.graph.model.user.UserEntity;
import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.UserRepository;
import com.example.graph.validate.ValidationException;
import com.example.graph.web.form.UserPublicForm;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class UserPublicConverter {
    private final NodeRepository nodeRepository;
    private final UserRepository userRepository;

    public UserPublicConverter(NodeRepository nodeRepository, UserRepository userRepository) {
        this.nodeRepository = nodeRepository;
        this.userRepository = userRepository;
    }

    public UserEntity toEntity(UserPublicForm form, OffsetDateTime now) {
        NodeEntity node = nodeRepository.findById(form.getNodeId())
            .orElseThrow(() -> new ValidationException("Node not found."));
        return userRepository.findByNodeId(form.getNodeId())
            .orElseGet(() -> {
                UserEntity user = new UserEntity();
                user.setId(UUID.randomUUID());
                user.setNode(node);
                user.setCreatedAt(now);
                return userRepository.save(user);
            });
    }

}
