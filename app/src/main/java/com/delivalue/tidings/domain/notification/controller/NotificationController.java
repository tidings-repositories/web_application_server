package com.delivalue.tidings.domain.notification.controller;

import com.delivalue.tidings.domain.notification.dto.NotificationCursorRequest;
import com.delivalue.tidings.domain.notification.dto.NotificationResponse;
import com.delivalue.tidings.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping
    public ResponseEntity<List<NotificationResponse>> requestNotifications(
            @AuthenticationPrincipal String userId,
            @RequestBody(required = false) NotificationCursorRequest body
    ) {
        LocalDateTime cursorTime = (body != null && body.getSentAt() != null)
                ? body.getSentAt().atZoneSameInstant(ZoneOffset.UTC).toLocalDateTime()
                : null;
        List<NotificationResponse> result = notificationService.getNotifications(userId, cursorTime);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{notificationId}/open")
    public ResponseEntity<?> requestMarkOpened(
            @AuthenticationPrincipal String userId,
            @PathVariable("notificationId") String notificationId
    ) {
        notificationService.markOpened(userId, notificationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{notificationId}/dislike")
    public ResponseEntity<?> requestMarkDisliked(
            @AuthenticationPrincipal String userId,
            @PathVariable("notificationId") String notificationId
    ) {
        notificationService.markDisliked(userId, notificationId);
        return ResponseEntity.ok().build();
    }
}
