package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.UserPairInteractionSummary;
import com.delivalue.tidings.domain.data.entity.embed.UserPairId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPairInteractionSummaryRepository extends JpaRepository<UserPairInteractionSummary, UserPairId> {

    List<UserPairInteractionSummary> findAllByIdSourceUserId(String sourceUserId);

    @Query("""
        SELECT u FROM UserPairInteractionSummary u
        WHERE u.id.sourceUserId = :sourceUserId
        ORDER BY u.interactionScore DESC
        """)
    List<UserPairInteractionSummary> findAllBySourceUserIdOrderByScore(@Param("sourceUserId") String sourceUserId);
}
