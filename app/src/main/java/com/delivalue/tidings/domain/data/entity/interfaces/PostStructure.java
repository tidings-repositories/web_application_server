package com.delivalue.tidings.domain.data.entity.interfaces;

import java.time.LocalDateTime;

public interface PostStructure {
    String getId();
    String getUserId();
    String getUserName();
    String getProfileImage();
    BadgeStructure getBadge();
    ContentStructure getContent();
    Integer getCommentCount();
    Integer getLikeCount();
    Integer getScrapCount();
    boolean isOrigin();
    LocalDateTime getCreatedAt();
    LocalDateTime getDeletedAt();
    String getOriginalPostId();
    String getOriginalUserId();
}
