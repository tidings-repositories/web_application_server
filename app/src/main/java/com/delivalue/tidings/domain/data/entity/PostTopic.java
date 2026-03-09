package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 기능 9: 포스트-토픽 연결 (사용자 직접 태그, 영구 데이터)
 * 대응: Topic Social Proof TSPS (DOC_17/19)
 * ⚠️ 주의3 반영: MySQL → MongoDB 이동 (postId가 MongoDB ObjectId이므로 동일 DB에 저장)
 */
@Document(collection = "post_topics")
@CompoundIndexes({
    @CompoundIndex(name = "idx_post_topic", def = "{'postId': 1, 'topicId': 1}", unique = true),
    @CompoundIndex(name = "idx_topic_at", def = "{'topicId': 1, 'createdAt': -1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostTopic {

    @Id
    private ObjectId id;

    private String postId;

    // Topic.id (MySQL) 참조 — 크로스 DB 참조는 앱 레이어에서 조인
    private Long topicId;

    // USER: 작성자 직접 태그, SYSTEM: 자동 분류 (PostTopicAnnotation과 구분)
    @Builder.Default
    private String tagSource = "USER";

    private LocalDateTime createdAt;
}
