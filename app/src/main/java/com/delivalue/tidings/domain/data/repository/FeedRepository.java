package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.Feed;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeedRepository extends MongoRepository<Feed, ObjectId> {
}
