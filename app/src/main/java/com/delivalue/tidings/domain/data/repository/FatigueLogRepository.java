package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.FatigueLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FatigueLogRepository extends MongoRepository<FatigueLog, ObjectId> {

    List<FatigueLog> findAllByMemberIdAndSnapshotAtAfterOrderBySnapshotAtDesc(
        String memberId, LocalDateTime since);
}
