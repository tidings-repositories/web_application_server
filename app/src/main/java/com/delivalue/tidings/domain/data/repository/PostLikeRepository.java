package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.Like;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PostLikeRepository extends MongoRepository<Like, ObjectId> {
    Optional<Like> findByLikeUserIdAndPostId(String likeUserInternalId, String postId);
}
