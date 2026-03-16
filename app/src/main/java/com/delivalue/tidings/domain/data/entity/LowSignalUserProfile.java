package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 기능 28: 저시그널 사용자 판정 캐시 (30일 TTL)
 * 대응: Tweet Mixer LowSignalUserFilter (DOC_15)
 */
@Document(collection = "low_signal_user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LowSignalUserProfile {

    @Id
    private ObjectId id;

    private String memberId; // internalUserId

    // 저시그널 판정 이유 (NEW_USER / INACTIVE / SPARSE_INTERACTION / COLD_START)
    private String reason;

    // OON 피드 전용 전략 (GAME_PROFILE_BASED / TRENDING_ONLY / SIMILAR_NEW_USERS)
    private String fallbackStrategy;

    private LocalDateTime determinedAt;
    private LocalDateTime expiredAt; // 30일 TTL (재활성화 시 갱신)
}
