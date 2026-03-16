package com.delivalue.tidings.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
public class NotificationCursorRequest {
    @JsonProperty("sent_at")
    private OffsetDateTime sentAt;
}
