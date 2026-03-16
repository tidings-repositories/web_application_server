package com.delivalue.tidings.domain.notification.service;

import com.delivalue.tidings.domain.data.entity.CaretFeedbackHistory;
import com.delivalue.tidings.domain.data.entity.NotificationLog;
import com.delivalue.tidings.domain.data.repository.CaretFeedbackHistoryRepository;
import com.delivalue.tidings.domain.data.repository.NotificationLogRepository;
import com.delivalue.tidings.domain.notification.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationLogRepository notificationLogRepository;
    private final CaretFeedbackHistoryRepository caretFeedbackHistoryRepository;
    private final MongoTemplate mongoTemplate;

    public List<NotificationResponse> getNotifications(String receiverId, LocalDateTime cursorTime) {
        Query query = new Query();
        query.addCriteria(Criteria.where("receiverId").is(receiverId));

        if (cursorTime != null) {
            query.addCriteria(Criteria.where("sentAt").lt(cursorTime));
        }

        query.with(Sort.by(Sort.Direction.DESC, "sentAt"));
        query.limit(20);

        return mongoTemplate.find(query, NotificationLog.class).stream()
                .map(NotificationResponse::new)
                .toList();
    }

    public void markOpened(String receiverId, String notificationId) {
        ObjectId objectId = toObjectId(notificationId);
        NotificationLog log = notificationLogRepository.findById(objectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!log.getReceiverId().equals(receiverId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Query query = Query.query(Criteria.where("_id").is(objectId));
        Update update = new Update()
                .set("isOpened", true)
                .set("openedAt", LocalDateTime.now(ZoneOffset.UTC));
        mongoTemplate.updateFirst(query, update, NotificationLog.class);
    }

    public void markDisliked(String receiverId, String notificationId) {
        ObjectId objectId = toObjectId(notificationId);
        NotificationLog log = notificationLogRepository.findById(objectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!log.getReceiverId().equals(receiverId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Query query = Query.query(Criteria.where("_id").is(objectId));
        Update update = new Update().set("isDisliked", true);
        mongoTemplate.updateFirst(query, update, NotificationLog.class);

        CaretFeedbackHistory feedback = CaretFeedbackHistory.builder()
                .userId(receiverId)
                .targetType("NOTIFICATION")
                .targetId(notificationId)
                .feedbackType("DONT_LIKE")
                .feedbackAt(LocalDateTime.now(ZoneOffset.UTC))
                .build();
        caretFeedbackHistoryRepository.insert(feedback);
    }

    private ObjectId toObjectId(String id) {
        try {
            return new ObjectId(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
