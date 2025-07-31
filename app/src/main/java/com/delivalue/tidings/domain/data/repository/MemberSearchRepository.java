package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.MemberSearch;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberSearchRepository extends ElasticsearchRepository<MemberSearch, String> {

    @Query("""
    {
      "bool": {
        "must": [
          {
            "multi_match": {
              "query": "?0",
              "fields": ["public_id", "name", "bio"]
            }
          }
        ],
        "filter": [
          { "bool": { "must_not": [ { "exists": { "field": "deleted_at" } } ] } },
          { "bool": { "must_not": [ { "exists": { "field": "banned_at" } } ] } }
        ]
      }
    }
    """)
    List<MemberSearch> searchMembers(String keyword, Pageable pageable);
}
