package com.example.graph.service.user;

import com.example.graph.model.NodeEntity;
import com.example.graph.model.user.UserEntity;
import com.example.graph.repository.NodeRepository;
import com.example.graph.repository.UserRepository;
import com.example.graph.service.value.NodeValueService;
import com.example.graph.validate.ValidationException;
import com.example.graph.web.dto.UserDto;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final NodeRepository nodeRepository;
    private final NodeValueService nodeValueService;

    public UserService(UserRepository userRepository,
                       NodeRepository nodeRepository,
                       NodeValueService nodeValueService) {
        this.userRepository = userRepository;
        this.nodeRepository = nodeRepository;
        this.nodeValueService = nodeValueService;
    }

    public UserEntity createUserForNode(Long nodeId) {
        if (nodeId == null) {
            throw new IllegalArgumentException("Node is required.");
        }
        NodeEntity node = nodeRepository.findById(nodeId)
            .orElseThrow(() -> new IllegalArgumentException("Node not found."));
        if (userRepository.existsByNodeId(nodeId)) {
            throw new IllegalArgumentException("Selected node already has a user.");
        }
        OffsetDateTime now = OffsetDateTime.now();
        UserEntity user = new UserEntity();
        user.setNode(node);
        user.setCreatedAt(now);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<UserDto> listUsersDto() {
        OffsetDateTime now = OffsetDateTime.now();
        Map<Long, String> nodeNames = nodeValueService.getCurrentValues(now);
        return userRepository.findAll().stream()
            .map(user -> {
                return new UserDto(
                    user.getId(),
                    nodeNames.getOrDefault(user.getNode().getId(), "—")
                );
            })
            .toList();
    }

    public UserEntity getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new ValidationException("User not found."));
    }
}
