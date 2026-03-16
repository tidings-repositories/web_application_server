package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.Scrap;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScrapRepository extends MongoRepository<Scrap, ObjectId> {

    Optional<Scrap> findByUserIdAndPostId(String userId, String postId);

    boolean existsByUserIdAndPostId(String userId, String postId);

    List<Scrap> findAllByUserIdOrderByScrappedAtDesc(String userId);
}
