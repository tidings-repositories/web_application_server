package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Document(collection = "feeds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feed {

    @Id
    private ObjectId id;

    private String internalUserId;
    private String postId;
    private LocalDateTime createdAt;
    private Date expiredAt;

    // 기능 8: ScoredTweetsMixerPipelineConfig — 후보 경로 (DOC_26)
    @Builder.Default
    private String sourceType = "IN_NETWORK"; // IN_NETWORK / OON_SOCIAL / OON_INTEREST / TRENDING

    @Field(write = Field.Write.NON_NULL)
    private Double candidateScore;

    @Field(write = Field.Write.NON_NULL)
    private String candidateSource; // FOLLOW_FANOUT / UTEG / SIMCLUSTERS_ANN / FRS / EARLYBIRD

    @Builder.Default
    private Boolean isAlgorithmic = false;

    // 기능 41: Heavy Ranker 15종 예측 확률 보존 (DOC_22)
    // 키 목록: p_fav, p_repost, p_reply, p_click, p_linger, p_video_playback_50, p_scrap, p_share, p_profile_click, p_negative_feedback
    @Field(write = Field.Write.NON_NULL)
    private Map<String, Double> predictionScores;

    @Field(write = Field.Write.NON_NULL)
    private String modelVersion; // Heavy Ranker 모델 버전 — A/B 테스트 분석용
}
