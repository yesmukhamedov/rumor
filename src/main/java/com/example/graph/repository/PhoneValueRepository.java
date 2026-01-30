package com.example.graph.repository;

import com.example.graph.model.PhoneValueEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PhoneValueRepository extends JpaRepository<PhoneValueEntity, Long> {
    boolean existsByValue(String value);

    @Query("""
        select pv
        from PhoneValueEntity pv
        where (pv.createdAt is null or pv.createdAt <= :now)
          and (pv.expiredAt is null or :now <= pv.expiredAt)
          and pv.createdAt = (
              select max(pv2.createdAt)
              from PhoneValueEntity pv2
              where pv2.phone.id = pv.phone.id
                and (pv2.createdAt is null or pv2.createdAt <= :now)
                and (pv2.expiredAt is null or :now <= pv2.expiredAt)
          )
        """)
    List<PhoneValueEntity> findCurrentValues(@Param("now") OffsetDateTime now);

    @Query("""
        select pv
        from PhoneValueEntity pv
        where pv.phone.id = :phoneId
          and (pv.createdAt is null or pv.createdAt <= :now)
          and (pv.expiredAt is null or :now <= pv.expiredAt)
          and pv.createdAt = (
              select max(pv2.createdAt)
              from PhoneValueEntity pv2
              where pv2.phone.id = :phoneId
                and (pv2.createdAt is null or pv2.createdAt <= :now)
                and (pv2.expiredAt is null or :now <= pv2.expiredAt)
          )
        """)
    Optional<PhoneValueEntity> findCurrentValueByPhoneId(@Param("phoneId") Long phoneId,
                                                        @Param("now") OffsetDateTime now);
}
