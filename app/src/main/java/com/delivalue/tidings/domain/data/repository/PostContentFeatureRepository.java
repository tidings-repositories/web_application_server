package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.PostContentFeature;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostContentFeatureRepository extends MongoRepository<PostContentFeature, ObjectId> {

    Optional<PostContentFeature> findByPostId(String postId);

    List<PostContentFeature> findAllByPostIdIn(List<String> postIds);
}
