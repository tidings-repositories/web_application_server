package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.UserActionEvent;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserActionEventRepository extends MongoRepository<UserActionEvent, ObjectId> {

    List<UserActionEvent> findAllByActorIdAndActionTypeAndOccurredAtAfter(
        String actorId, String actionType, LocalDateTime since);

    List<UserActionEvent> findAllByTargetIdAndActionType(String targetId, String actionType);
}
