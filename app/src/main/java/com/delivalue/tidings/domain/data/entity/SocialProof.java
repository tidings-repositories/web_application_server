package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 기능 36: "A님이 좋아요" 소셜 컨텍스트 캐시 (48h TTL)
 * 대응: SocialContextFeatureHydrator, GraphJet UUG (DOC_16/24)
 */
@Document(collection = "social_proofs")
@CompoundIndexes({
    @CompoundIndex(name = "idx_viewer_post", def = "{'viewerId': 1, 'postId': 1}", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SocialProof {

    @Id
    private ObjectId id;

    private String viewerId;  // 이 소셜 증거를 볼 사용자 internalUserId
    private String postId;

    // 해당 포스트에 반응한 팔로잉 목록 (최대 3명)
    private List<String> likedByFollowingIds;
    private List<String> commentedByFollowingIds;
    private List<String> repostedByFollowingIds;

    private LocalDateTime cachedAt;
    private LocalDateTime expiredAt; // 48h TTL
}
