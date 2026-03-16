package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 기능 29: ML 자동 토픽 어노테이션 (30일 TTL)
 * 대응: TSPS 자동 어노테이션, CR-Mixer CertoTopic (DOC_14/19)
 */
@Document(collection = "post_topic_annotations")
@CompoundIndexes({
    @CompoundIndex(name = "idx_post_topic", def = "{'postId': 1, 'topicId': 1}"),
    @CompoundIndex(name = "idx_topic_score", def = "{'topicId': 1, 'confidenceScore': -1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostTopicAnnotation {

    @Id
    private ObjectId id;

    private String postId;
    private Long topicId; // Topic.id (MySQL) 참조

    // ML 모델 신뢰도 점수 (0.0~1.0)
    private Double confidenceScore;

    // 사용한 모델 식별자 (certoTopic_v2 등)
    private String modelName;

    private LocalDateTime annotatedAt;
    private LocalDateTime expiredAt; // 30일 TTL
}
