package com.example.graph.repository;

import com.example.graph.model.NodeValueEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NodeValueRepository extends JpaRepository<NodeValueEntity, Long> {
    @Query("""
        select nv
        from NodeValueEntity nv
        where (nv.createdAt is null or nv.createdAt <= :now)
          and (nv.expiredAt is null or :now <= nv.expiredAt)
          and nv.createdAt = (
              select max(nv2.createdAt)
              from NodeValueEntity nv2
              where nv2.node.id = nv.node.id
                and (nv2.createdAt is null or nv2.createdAt <= :now)
                and (nv2.expiredAt is null or :now <= nv2.expiredAt)
          )
        """)
    List<NodeValueEntity> findCurrentValues(@Param("now") OffsetDateTime now);

    @Query("""
        select nv
        from NodeValueEntity nv
        where nv.node.id = :nodeId
          and (nv.createdAt is null or nv.createdAt <= :now)
          and (nv.expiredAt is null or :now <= nv.expiredAt)
          and nv.createdAt = (
              select max(nv2.createdAt)
              from NodeValueEntity nv2
              where nv2.node.id = :nodeId
                and (nv2.createdAt is null or nv2.createdAt <= :now)
                and (nv2.expiredAt is null or :now <= nv2.expiredAt)
          )
        """)
    Optional<NodeValueEntity> findCurrentValueByNodeId(@Param("nodeId") Long nodeId,
                                                      @Param("now") OffsetDateTime now);
}
