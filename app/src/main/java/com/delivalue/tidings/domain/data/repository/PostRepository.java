package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {}
