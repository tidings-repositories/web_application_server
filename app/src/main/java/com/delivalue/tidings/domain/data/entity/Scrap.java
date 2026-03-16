package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * 기능 1: 개별 스크랩 추적 — USS Bookmark Signal 원천 (DOC_03)
 * Post.scrapCount는 집계 카운트만 보존, 실제 스크랩 관계는 여기서 관리
 */
@Document(collection = "scraps")
@CompoundIndexes({
    @CompoundIndex(name = "idx_user_post", def = "{'userId': 1, 'postId': 1}", unique = true),
    @CompoundIndex(name = "idx_user_at", def = "{'userId': 1, 'scrappedAt': -1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Scrap {

    @Id
    private ObjectId id;

    private String postId;
    private String userId; // internalUserId
    private String postCreatedAt; // USS 신호 시간 필터링용
    private LocalDateTime scrappedAt;
}
