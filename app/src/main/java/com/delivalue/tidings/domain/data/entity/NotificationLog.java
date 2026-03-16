package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * 기능 12: 알림 전송 이력 — 피로도 관리 + 3단계 필터링 추적
 * 기능 22: candidateRank, candidateScore, filteringStage, rejectionReason 추가
 * 대응: PushService FatiguePredicate, HistoryWriter (DOC_29/30)
 */
@Document(collection = "notification_logs")
@CompoundIndexes({
    @CompoundIndex(name = "idx_receiver_at", def = "{'receiverId': 1, 'sentAt': -1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationLog {

    @Id
    private ObjectId id;

    @Indexed
    private String receiverId; // internalUserId

    // LIKE / COMMENT / FOLLOW / REPOST / MENTION / PUSH_RECOMMENDED_POST 등
    private String notificationType;

    @Field(write = Field.Write.NON_NULL)
    private String postId;

    @Field(write = Field.Write.NON_NULL)
    private String triggerMemberId; // 알림을 유발한 사용자

    // PUSH / IN_APP / EMAIL
    private String channel;

    private LocalDateTime sentAt;

    @Builder.Default
    private Boolean isOpened = false;

    @Field(write = Field.Write.NON_NULL)
    private LocalDateTime openedAt;

    @Builder.Default
    private Boolean isDisliked = false; // "관심없음" 피드백

    @Field(write = Field.Write.NON_NULL)
    private String candidateSource; // 알림 후보 생성 소스

    // 기능 22: PushService Heavy Ranker 최종 순위 (DOC_30)
    @Field(write = Field.Write.NON_NULL)
    private Integer candidateRank;

    @Field(write = Field.Write.NON_NULL)
    private Double candidateScore;

    // PRE_RANK_FILTER / RANK_FILTER / SENT
    @Field(write = Field.Write.NON_NULL)
    private String filteringStage;

    // FATIGUE_LIMIT / LOW_SCORE / OPTED_OUT / SAFETY_FILTER / DUPLICATE
    @Field(write = Field.Write.NON_NULL)
    private String rejectionReason;
}
