package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.SafetyLevelConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SafetyLevelConfigRepository extends JpaRepository<SafetyLevelConfig, Long> {

    Optional<SafetyLevelConfig> findBySurfaceName(String surfaceName);
}
