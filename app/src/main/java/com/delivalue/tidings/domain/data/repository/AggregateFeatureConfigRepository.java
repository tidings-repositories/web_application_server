package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.AggregateFeatureConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AggregateFeatureConfigRepository extends JpaRepository<AggregateFeatureConfig, Long> {

    Optional<AggregateFeatureConfig> findByFeatureName(String featureName);

    List<AggregateFeatureConfig> findAllByIsActiveTrue();
}
