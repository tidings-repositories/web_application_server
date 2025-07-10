package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, ObjectId> {
    List<Comment> findByPostIdOrderByCreatedAtAsc(String postId);
}
