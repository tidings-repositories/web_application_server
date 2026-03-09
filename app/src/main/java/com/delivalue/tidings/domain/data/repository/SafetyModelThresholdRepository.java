package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.SafetyModelThreshold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SafetyModelThresholdRepository extends JpaRepository<SafetyModelThreshold, Long> {

    Optional<SafetyModelThreshold> findByModelName(String modelName);

    List<SafetyModelThreshold> findAllByIsActiveTrue();
}
