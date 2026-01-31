package com.example.graph.service.user;

import com.example.graph.model.user.ProfileEntity;
import com.example.graph.repository.ProfileRepository;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Transactional(readOnly = true)
    public Optional<ProfileEntity> getCurrentProfile(Long userId, OffsetDateTime now) {
        return profileRepository.findCurrentProfileByUserId(userId, now);
    }

    @Transactional(readOnly = true)
    public Map<Long, ProfileEntity> getCurrentProfiles(OffsetDateTime now) {
        return profileRepository.findCurrentProfiles(now).stream()
            .collect(Collectors.toMap(value -> value.getUser().getId(), value -> value, (a, b) -> a));
    }
}
