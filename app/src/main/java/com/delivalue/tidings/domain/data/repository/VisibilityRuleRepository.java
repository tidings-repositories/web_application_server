package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.VisibilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisibilityRuleRepository extends JpaRepository<VisibilityRule, Long> {

    List<VisibilityRule> findAllByTargetTypeAndIsActiveTrueOrderByPriorityAsc(String targetType);

    List<VisibilityRule> findAllByTargetTypeAndSurfaceAndIsActiveTrueOrderByPriorityAsc(String targetType, String surface);
}
