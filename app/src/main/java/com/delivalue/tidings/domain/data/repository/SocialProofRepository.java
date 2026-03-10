package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.SocialProof;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocialProofRepository extends MongoRepository<SocialProof, ObjectId> {

    Optional<SocialProof> findByViewerIdAndPostId(String viewerId, String postId);

    List<SocialProof> findAllByViewerIdAndPostIdIn(String viewerId, List<String> postIds);
}
