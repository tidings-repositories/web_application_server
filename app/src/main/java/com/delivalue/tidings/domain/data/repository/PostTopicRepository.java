package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.PostTopic;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTopicRepository extends MongoRepository<PostTopic, ObjectId> {

    List<PostTopic> findAllByPostId(String postId);

    List<PostTopic> findAllByTopicIdOrderByCreatedAtDesc(Long topicId);

    boolean existsByPostIdAndTopicId(String postId, Long topicId);
}
