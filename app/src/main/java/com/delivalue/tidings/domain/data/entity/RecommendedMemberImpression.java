package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

/**
 * 기능 27: 팔로우 추천 노출 이력 — 피로도 관리 + 효과 측정
 * 대응: WTF Impression Store, ImpressionBasedFatigueRanker (DOC_17)
 */
@Document(collection = "recommended_member_impressions")
@CompoundIndexes({
    @CompoundIndex(name = "idx_viewer_candidate", def = "{'viewerId': 1, 'candidateMemberId': 1}"),
    @CompoundIndex(name = "idx_viewer_at", def = "{'viewerId': 1, 'impressedAt': -1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendedMemberImpression {

    @Id
    private ObjectId id;

    private String viewerId;       // 추천을 받은 사용자 internalUserId
    private String candidateMemberId; // 추천된 사용자 internalUserId

    // WTF_FOLLOW / SIMCLUSTERS / FRS_SOCIAL / FRS_INTEREST
    private String recommendationSource;

    private LocalDateTime impressedAt;

    // 추천 후 팔로우 전환 여부
    @Builder.Default
    private Boolean isFollowed = false;

    @Field(write = Field.Write.NON_NULL)
    private LocalDateTime followedAt;

    // 추천 후 "관심없음" 피드백 여부
    @Builder.Default
    private Boolean isDismissed = false;
}
