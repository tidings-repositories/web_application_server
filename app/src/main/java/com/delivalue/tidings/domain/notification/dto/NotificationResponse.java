package com.delivalue.tidings.domain.notification.dto;

import com.delivalue.tidings.domain.data.entity.NotificationLog;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponse {
    private final String id;
    private final String notification_type;
    private final String post_id;
    private final String trigger_member_id;
    private final String channel;
    private final LocalDateTime sent_at;
    private final boolean is_opened;
    private final LocalDateTime opened_at;
    private final boolean is_disliked;
    private final String candidate_source;

    public NotificationResponse(NotificationLog log) {
        this.id = log.getId().toHexString();
        this.notification_type = log.getNotificationType();
        this.post_id = log.getPostId();
        this.trigger_member_id = log.getTriggerMemberId();
        this.channel = log.getChannel();
        this.sent_at = log.getSentAt();
        this.is_opened = Boolean.TRUE.equals(log.getIsOpened());
        this.opened_at = log.getOpenedAt();
        this.is_disliked = Boolean.TRUE.equals(log.getIsDisliked());
        this.candidate_source = log.getCandidateSource();
    }
}
