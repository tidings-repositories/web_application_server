package com.delivalue.tidings.domain.data.repository;

import com.delivalue.tidings.domain.data.entity.CaretFeedbackHistory;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CaretFeedbackHistoryRepository extends MongoRepository<CaretFeedbackHistory, ObjectId> {

    List<CaretFeedbackHistory> findAllByUserIdAndFeedbackAtAfter(String userId, LocalDateTime since);

    long countByUserIdAndFeedbackAtAfter(String userId, LocalDateTime since);
}
