package com.example.graph.repository;

import com.example.graph.model.EdgeValueEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EdgeValueRepository extends JpaRepository<EdgeValueEntity, Long> {
    @Query("""
        select ev
        from EdgeValueEntity ev
        where (ev.createdAt is null or ev.createdAt <= :now)
          and (ev.expiredAt is null or :now <= ev.expiredAt)
          and ev.createdAt = (
              select max(ev2.createdAt)
              from EdgeValueEntity ev2
              where ev2.edge.id = ev.edge.id
                and (ev2.createdAt is null or ev2.createdAt <= :now)
                and (ev2.expiredAt is null or :now <= ev2.expiredAt)
          )
        """)
    List<EdgeValueEntity> findCurrentValues(@Param("now") OffsetDateTime now);

    @Query("""
        select ev
        from EdgeValueEntity ev
        where ev.edge.id = :edgeId
          and (ev.createdAt is null or ev.createdAt <= :now)
          and (ev.expiredAt is null or :now <= ev.expiredAt)
          and ev.createdAt = (
              select max(ev2.createdAt)
              from EdgeValueEntity ev2
              where ev2.edge.id = :edgeId
                and (ev2.createdAt is null or ev2.createdAt <= :now)
                and (ev2.expiredAt is null or :now <= ev2.expiredAt)
          )
        """)
    Optional<EdgeValueEntity> findCurrentValueByEdgeId(@Param("edgeId") Long edgeId,
                                                      @Param("now") OffsetDateTime now);

    @Query("""
        select distinct ev.value
        from EdgeValueEntity ev
        where ev.edge.fromNode is null
          and (ev.createdAt is null or ev.createdAt <= :now)
          and (ev.expiredAt is null or :now <= ev.expiredAt)
        """)
    List<String> findDistinctCurrentPublicValues(@Param("now") OffsetDateTime now);
}
