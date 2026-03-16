package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.RecommendedMemberImpression;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecommendedMemberImpressionRepository extends MongoRepository<RecommendedMemberImpression, ObjectId> {

    boolean existsByViewerIdAndCandidateMemberId(String viewerId, String candidateMemberId);

    List<RecommendedMemberImpression> findAllByViewerIdAndImpressedAtAfter(
        String viewerId, LocalDateTime since);
}
