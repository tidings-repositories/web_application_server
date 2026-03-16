package com.delivalue.tidings.domain.data.entity;

import com.delivalue.tidings.domain.data.entity.interfaces.BadgeStructure;
import com.delivalue.tidings.domain.data.entity.interfaces.ContentStructure;
import com.delivalue.tidings.domain.data.entity.interfaces.PostMediaStructure;
import com.delivalue.tidings.domain.data.entity.interfaces.PostStructure;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document(collection = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post implements PostStructure {

    @Id
    private String id;

    private String internalUserId;
    private String userId;
    private String userName;
    private String profileImage;
    private Badge badge;
    private Content content;
    private Integer commentCount;
    private Integer likeCount;
    private Integer scrapCount;
    private boolean isOrigin;
    private LocalDateTime createdAt;

    @Field(write = Field.Write.NON_NULL)
    private LocalDateTime deletedAt;

    @Field(write = Field.Write.NON_NULL)
    private String originalPostId;
    @Field(write = Field.Write.NON_NULL)
    private String originalUserId;

    // 기능 2: 노출/집계 지표 — Earlybird 실시간 피처
    @Builder.Default
    private Integer viewCount = 0;

    @Builder.Default
    private Integer repostCount = 0;

    private String languageCode;

    @Builder.Default
    private Boolean hasMedia = false;

    // 기능 10 + 18 통합: 안전 점수 (주의2 반영 — nsfwScore 생략)
    private Double toxicityScore;       // pToxicity — Earlybird Light Ranker (DOC_08)
    private Double nsfwMediaScore;      // pNSFWMedia — CLIP 이미지 분석 (DOC_08)
    private Double nsfwTextScore;       // pNSFWText — BERT 텍스트 분석 (DOC_08)
    private Double spamScore;           // PSPAMMY_TWEET_SCORE — Earlybird (DOC_21)
    private Double pBlockScore;         // pBlock — Light Ranker 핵심 피처

    private Map<String, Double> abuseScores; // pAbuse 8개 멀티라벨 (DOC_08)

    @Builder.Default
    private List<String> safetyLabels = new ArrayList<>(); // TweetSafetyLabel — VisibilityLib (DOC_28)

    @Field(write = Field.Write.NON_NULL)
    private LocalDateTime safetyLabelUpdatedAt;

    // 기능 17: 랭킹 피처 필드 — Earlybird Light/Heavy Ranker (DOC_12/22)
    @Builder.Default
    private Boolean isReply = false;

    @Builder.Default
    private Boolean isRetweet = false; // !isOrigin 파생, 인덱스용

    @Builder.Default
    private Boolean hasCard = false;

    @Builder.Default
    private Boolean hasUrl = false;

    @Builder.Default
    private Integer textLength = 0;

    @Builder.Default
    private Integer videoViewCount = 0;

    @Builder.Default
    private Boolean isBounced = false;

    @Field(write = Field.Write.NON_NULL)
    private String bounceReason; // SPAM / LOW_QUALITY / POLICY_VIOLATION

    @Builder.Default
    private Boolean isPushEligible = true;

    @Field(write = Field.Write.NON_NULL)
    private Map<String, String> cardInfo; // 링크 카드 메타데이터 (Recos Injector, DOC_20)

    @Builder.Default
    private List<String> semanticCoreAnnotations = new ArrayList<>(); // CR-Mixer 토픽 매칭 (DOC_14)

    @Builder.Default
    private String visibilityAction = "ALLOW"; // ALLOW / DOWNRANK / INTERSTITIAL / DROP

    // 기능 31: 게이밍 SNS 특화
    @Field(write = Field.Write.NON_NULL)
    private Long gameId; // Topic.id 참조 (게임 타이틀)

    @Builder.Default
    private List<String> gameGenres = new ArrayList<>(); // MOBA / FPS / RPG 등

    @Builder.Default
    private List<String> gamePlatforms = new ArrayList<>(); // PC / MOBILE / PS5 등

    @Builder.Default
    private String postSubType = "TEXT"; // TEXT / CLIP / SCREENSHOT / ACHIEVEMENT / LFG / GUIDE / TOURNAMENT_RECAP

    @Field(write = Field.Write.NON_NULL)
    private String gameVersion; // 게임 버전/패치

    // 기능 46: 대화 스레딩 — ConversationService (DOC_26)
    @Field(write = Field.Write.NON_NULL)
    private String conversationId;

    @Field(write = Field.Write.NON_NULL)
    private String inReplyToPostId;

    @Field(write = Field.Write.NON_NULL)
    private String inReplyToMemberId;

    @Field(write = Field.Write.NON_NULL)
    private String selfThreadId;

    @Builder.Default
    private Integer conversationDepth = 0;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Badge implements BadgeStructure {
        private Integer id;
        private String name;
        private String url;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Content implements ContentStructure {
        private String text;
        private List<PostMedia> media;
        private List<String> tag;

        @Override
        public List<PostMediaStructure> getMedia() {
            return new ArrayList<>(media);
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PostMedia implements PostMediaStructure {
        private String type; // image/video
        private String url;
    }
}
