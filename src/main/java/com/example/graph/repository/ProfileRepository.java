package com.example.graph.repository;

import com.example.graph.model.user.ProfileEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    List<ProfileEntity> findByUserId(Long userId);

    Optional<ProfileEntity> findFirstByExternalUserUuidOrderByCreatedAtDesc(UUID uuid);

    Optional<ProfileEntity> findFirstByUserIdAndExpiredAtIsNull(Long userId);

    @Query("""
        select p
        from ProfileEntity p
        where (p.createdAt is null or p.createdAt <= :now)
          and (p.expiredAt is null or :now <= p.expiredAt)
          and p.createdAt = (
              select max(p2.createdAt)
              from ProfileEntity p2
              where p2.user.id = p.user.id
                and (p2.createdAt is null or p2.createdAt <= :now)
                and (p2.expiredAt is null or :now <= p2.expiredAt)
          )
        """)
    List<ProfileEntity> findCurrentProfiles(@Param("now") OffsetDateTime now);

    @Query("""
        select p
        from ProfileEntity p
        where p.user.id = :userId
          and (p.createdAt is null or p.createdAt <= :now)
          and (p.expiredAt is null or :now <= p.expiredAt)
          and p.createdAt = (
              select max(p2.createdAt)
              from ProfileEntity p2
              where p2.user.id = :userId
                and (p2.createdAt is null or p2.createdAt <= :now)
                and (p2.expiredAt is null or :now <= p2.expiredAt)
          )
        """)
    Optional<ProfileEntity> findCurrentProfileByUserId(@Param("userId") Long userId,
                                                     @Param("now") OffsetDateTime now);
}
