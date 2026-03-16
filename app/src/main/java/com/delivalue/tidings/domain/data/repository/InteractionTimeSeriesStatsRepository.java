package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.InteractionTimeSeriesStats;
import com.delivalue.tidings.domain.data.entity.embed.InteractionTimeSeriesId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteractionTimeSeriesStatsRepository extends JpaRepository<InteractionTimeSeriesStats, InteractionTimeSeriesId> {

    List<InteractionTimeSeriesStats> findAllByIdSourceUserIdAndIdTargetUserId(String sourceUserId, String targetUserId);
}
