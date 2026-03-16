package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.MemberMute;
import com.delivalue.tidings.domain.data.entity.embed.MemberRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberMuteRepository extends JpaRepository<MemberMute, MemberRelationId> {

    List<MemberMute> findAllByIdBlockerId(String blockerId);

    boolean existsByIdBlockerIdAndIdTargetId(String blockerId, String targetId);
}
