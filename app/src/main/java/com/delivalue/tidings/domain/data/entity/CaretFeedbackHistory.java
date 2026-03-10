package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 기능 25: "관심없음" 피드백 이력 — 피로도 예측 모델 입력
 * 대응: NtabCaretClickFatiguePredicate (DOC_29)
 */
@Document(collection = "caret_feedback_history")
@CompoundIndexes({
    @CompoundIndex(name = "idx_user_at", def = "{'userId': 1, 'feedbackAt': -1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaretFeedbackHistory {

    @Id
    private ObjectId id;

    private String userId; // internalUserId

    private String targetType; // POST / NOTIFICATION / RECOMMENDED_MEMBER

    private String targetId;

    // DONT_LIKE / NOT_RELEVANT / SEEN_TOO_OFTEN / OFFENSIVE / SPAM
    private String feedbackType;

    private LocalDateTime feedbackAt;
}
