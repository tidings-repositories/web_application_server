package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.MemberRetweetMute;
import com.delivalue.tidings.domain.data.entity.embed.MemberRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRetweetMuteRepository extends JpaRepository<MemberRetweetMute, MemberRelationId> {

    List<MemberRetweetMute> findAllByIdBlockerId(String blockerId);

    boolean existsByIdBlockerIdAndIdTargetId(String blockerId, String targetId);
}
