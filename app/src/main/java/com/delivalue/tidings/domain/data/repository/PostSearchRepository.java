package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.PostSearch;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostSearchRepository extends ElasticsearchRepository<PostSearch, String> {

    @Query("""
    {
      "bool": {
        "must": [
          {
            "multi_match": {
              "query": "?0",
              "fields": ["userId", "userName", "content.text", "content.tag"]
            }
          }
        ],
        "filter": [
          { "bool": { "must_not": [ { "exists": { "field": "deletedAt" } } ] } }
        ]
      }
    }
    """)
    List<PostSearch> searchPosts(String keyword, Pageable pageable);
}
