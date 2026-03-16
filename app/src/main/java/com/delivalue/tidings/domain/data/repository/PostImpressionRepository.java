package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.PostImpression;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostImpressionRepository extends MongoRepository<PostImpression, ObjectId> {

    boolean existsByViewerIdAndPostId(String viewerId, String postId);

    List<PostImpression> findAllByViewerIdAndViewedAtAfter(String viewerId, LocalDateTime since);
}
