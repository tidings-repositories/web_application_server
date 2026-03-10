package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 기능 40: NLP/비전 콘텐츠 피처 캐시 (44개 피처)
 * 대응: TweetMetaDataFeatureHydrator (DOC_24/27)
 */
@Document(collection = "post_content_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostContentFeature {

    @Id
    private ObjectId id;

    private String postId;

    // 텍스트 NLP 피처
    @Field(write = Field.Write.NON_NULL)
    private String detectedLanguage;

    @Field(write = Field.Write.NON_NULL)
    private List<String> namedEntities; // 개체명 인식 결과

    @Field(write = Field.Write.NON_NULL)
    private List<String> keywords; // 핵심 키워드

    @Field(write = Field.Write.NON_NULL)
    private Double sentimentScore; // 감정 점수 (-1.0~1.0)

    // 이미지/비디오 비전 피처
    @Field(write = Field.Write.NON_NULL)
    private List<String> imageLabels; // CLIP 이미지 라벨

    @Field(write = Field.Write.NON_NULL)
    private Double imageAestheticScore; // 이미지 심미성 점수

    // SimClusters 임베딩 (포스트 레벨)
    @Field(write = Field.Write.NON_NULL)
    private Map<String, Double> simClustersEmbedding; // clusterId → score

    // Heavy Ranker 입력용 숫자형 피처 벡터 (44개)
    @Field(write = Field.Write.NON_NULL)
    private Map<String, Double> numericalFeatures;

    private LocalDateTime extractedAt;
    private LocalDateTime expiredAt; // 72h TTL
}
