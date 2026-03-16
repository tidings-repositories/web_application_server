package com.delivalue.tidings.domain.topic.controller;

import com.delivalue.tidings.domain.topic.dto.PostTopicRequest;
import com.delivalue.tidings.domain.topic.dto.TopicResponse;
import com.delivalue.tidings.domain.topic.service.TopicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @GetMapping("/topic")
    public ResponseEntity<List<TopicResponse>> requestTopicList(
            @RequestParam(required = false) String category
    ) {
        List<TopicResponse> result = topicService.getAllTopics(category);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/post/{postId}/topic")
    public ResponseEntity<List<TopicResponse>> requestPostTopics(
            @PathVariable("postId") String postId
    ) {
        List<TopicResponse> result = topicService.getPostTopics(postId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/post/{postId}/topic")
    public ResponseEntity<?> requestTagPost(
            @AuthenticationPrincipal String userId,
            @PathVariable("postId") String postId,
            @Valid @RequestBody PostTopicRequest body
    ) {
        topicService.tagPostWithTopic(userId, postId, body.getTopicId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/{postId}/topic/{topicId}")
    public ResponseEntity<?> requestUntagPost(
            @AuthenticationPrincipal String userId,
            @PathVariable("postId") String postId,
            @PathVariable("topicId") Long topicId
    ) {
        topicService.untagPostFromTopic(userId, postId, topicId);
        return ResponseEntity.noContent().build();
    }
}
