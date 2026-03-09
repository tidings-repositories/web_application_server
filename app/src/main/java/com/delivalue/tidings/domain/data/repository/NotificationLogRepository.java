package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.NotificationLog;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationLogRepository extends MongoRepository<NotificationLog, ObjectId> {

    List<NotificationLog> findAllByReceiverIdOrderBySentAtDesc(String receiverId);

    List<NotificationLog> findAllByReceiverIdAndSentAtAfter(String receiverId, LocalDateTime since);

    long countByReceiverIdAndSentAtAfter(String receiverId, LocalDateTime since);
}
