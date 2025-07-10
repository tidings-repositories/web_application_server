package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    Optional<Post> findByIdAndDeletedAtIsNull(String id);

    List<Post> findByIdInAndDeletedAtIsNull(List<String> ids);

    Optional<Post> findByOriginalPostIdAndInternalUserId(String postId, String id);
}
