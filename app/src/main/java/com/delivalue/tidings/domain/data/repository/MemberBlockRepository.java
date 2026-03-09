package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.MemberBlock;
import com.delivalue.tidings.domain.data.entity.embed.MemberRelationId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberBlockRepository extends JpaRepository<MemberBlock, MemberRelationId> {

    List<MemberBlock> findAllByIdBlockerId(String blockerId);

    boolean existsByIdBlockerIdAndIdTargetId(String blockerId, String targetId);
}
