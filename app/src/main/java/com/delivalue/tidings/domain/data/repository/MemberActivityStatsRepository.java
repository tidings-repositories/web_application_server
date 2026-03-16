package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.MemberActivityStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberActivityStatsRepository extends JpaRepository<MemberActivityStats, String> {
}
