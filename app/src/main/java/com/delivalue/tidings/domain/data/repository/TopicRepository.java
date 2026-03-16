package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    List<Topic> findAllByCategory(String category);

    List<Topic> findAllByCategoryAndIsActiveTrue(String category);

    List<Topic> findAllByParentTopicId(Long parentTopicId);

    List<Topic> findAllByIsRecommendableTrue();
}
