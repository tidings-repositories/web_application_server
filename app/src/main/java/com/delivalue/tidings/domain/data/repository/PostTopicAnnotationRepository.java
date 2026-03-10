package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.PostTopicAnnotation;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostTopicAnnotationRepository extends MongoRepository<PostTopicAnnotation, ObjectId> {

    List<PostTopicAnnotation> findAllByPostIdOrderByConfidenceScoreDesc(String postId);

    List<PostTopicAnnotation> findAllByTopicIdOrderByAnnotatedAtDesc(Long topicId);
}
