package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Integer> {
}
