package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 기능 12: 알림 피로도 감사 이력 (Redis 카운터의 스냅샷)
 * 대응: PushService FatiguePredicate (DOC_29)
 */
@Document(collection = "fatigue_logs")
@CompoundIndexes({
    @CompoundIndex(name = "idx_member_at", def = "{'memberId': 1, 'snapshotAt': -1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FatigueLog {

    @Id
    private ObjectId id;

    private String memberId; // internalUserId

    // 스냅샷 시점의 피로도 카운터 값
    private Integer dailyCount;  // 당일 알림 발송 횟수
    private Integer weeklyCount; // 주간 알림 발송 횟수

    // 피로도 임계값 초과로 차단된 알림 정보
    private String blockedNotificationType;
    private String blockedCandidateSource;

    private LocalDateTime snapshotAt;
}
