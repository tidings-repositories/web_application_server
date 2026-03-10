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
 * 기능 2: 포스트 노출 이력 — 중복 노출 방지 + 드웰타임 버켓
 * 기능 44: dwellTimeMs, isSkipped, clickDwellBucket 추가
 * 대응: PreviouslySeenTweetsFilter, ImpressionBloomFilter (DOC_12/26)
 */
@Document(collection = "post_impressions")
@CompoundIndexes({
    @CompoundIndex(name = "idx_viewer_post", def = "{'viewerId': 1, 'postId': 1}"),
    @CompoundIndex(name = "idx_viewer_at", def = "{'viewerId': 1, 'viewedAt': -1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostImpression {

    @Id
    private ObjectId id;

    private String postId;
    private String viewerId; // internalUserId

    private LocalDateTime viewedAt;

    // HOME_FEED / OON_FEED / SEARCH / PROFILE / NOTIFICATION
    private String displayLocation;

    @Builder.Default
    private Boolean isLinger = false; // 체류 기준(5초) 충족 여부

    @Field(write = Field.Write.NON_NULL)
    private Double watchCompletionRate; // 비디오 시청 완료율 (0.0~1.0)

    // 기능 44: USS GoodTweetClick 버켓 기반 분류 (DOC_03)
    @Field(write = Field.Write.NON_NULL)
    private Long dwellTimeMs; // 실제 체류 시간 (밀리초)

    @Builder.Default
    private Boolean isSkipped = false; // 빠르게 스킵한 경우 (Heavy Ranker 부정 레이블)

    @Field(write = Field.Write.NON_NULL)
    private String clickDwellBucket; // NONE / 2S / 5S / 10S / 30S
}
