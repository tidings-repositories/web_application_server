package com.delivalue.tidings.domain.data.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 기능 1: 통합 사용자 행동 이벤트 스트림 (30종 actionType)
 * 대응: UUA Unified User Actions (DOC_02/15)
 *
 * actionType 30종:
 *   기능 1 (21종): POST_LIKE, POST_UNLIKE, POST_SCRAP, POST_UNSCRAP, POST_COMMENT, POST_REPOST,
 *     POST_QUOTE, POST_REPORT, POST_DONT_LIKE, POST_VIEW, POST_LINGER, POST_CLICK,
 *     VIDEO_WATCH, MEMBER_FOLLOW, MEMBER_UNFOLLOW, MEMBER_BLOCK, MEMBER_UNBLOCK,
 *     MEMBER_MUTE, MEMBER_UNMUTE, PROFILE_VIEW, NOTIFICATION_OPEN
 *   기능 13 (2종): POST_SHARE, NTAB_CLICK
 *   기능 44 (7종): POST_GOOD_CLICK_2S, POST_GOOD_CLICK_5S, POST_GOOD_CLICK_10S,
 *     POST_GOOD_CLICK_30S, PROFILE_GOOD_CLICK_10S, PROFILE_GOOD_CLICK_20S, PROFILE_GOOD_CLICK_30S
 */
@Document(collection = "user_action_events")
@CompoundIndexes({
    @CompoundIndex(name = "idx_actor_type_at", def = "{'actorId': 1, 'actionType': 1, 'occurredAt': -1}"),
    @CompoundIndex(name = "idx_target_type_at", def = "{'targetId': 1, 'actionType': 1, 'occurredAt': -1}")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserActionEvent {

    @Id
    private ObjectId id;

    @Indexed
    private String actorId; // 행동을 한 사용자 internalUserId

    private String actionType; // 30종 ENUM 값

    private String targetType; // POST / MEMBER / COMMENT / NOTIFICATION

    @Indexed
    private String targetId; // 대상 ID (postId / memberId 등)

    private String targetAuthorId; // 대상 콘텐츠 작성자 ID (피처 추출용)

    private LocalDateTime occurredAt;

    // 행동 발생 위치 (HOME_FEED / OON_FEED / SEARCH / PROFILE / NOTIFICATION)
    private String surface;

    // 행동 관련 수치 (linger 시간(ms), 비디오 시청률 등)
    @Field(write = Field.Write.NON_NULL)
    private Map<String, Object> metadata;
}
