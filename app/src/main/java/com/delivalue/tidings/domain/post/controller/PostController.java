package com.delivalue.tidings.domain.post.controller;

import com.delivalue.tidings.common.TokenProvider;
import com.delivalue.tidings.domain.data.entity.Post;
import com.delivalue.tidings.domain.post.dto.PostCreateRequest;
import com.delivalue.tidings.domain.post.dto.PostResponse;
import com.delivalue.tidings.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final TokenProvider tokenProvider;

    @PostMapping("/recent")
    public ResponseEntity<List<PostResponse>> requestRecentPostList(@RequestBody Map<String, String> body) {
        String cursorId = (String) body.get("postId");
        OffsetDateTime requestCursor = body.get("createdAt") != null ? OffsetDateTime.parse((String) body.get("createdAt")) : null;
        LocalDateTime cursorTime = requestCursor != null ? requestCursor.toLocalDateTime() : null;

        try {
            List<PostResponse> result = this.postService.getRecentPostByCursor(cursorId, cursorTime);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.printf("Catch Error /post/recent: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> requestPost(@PathVariable("postId") String postId) {
        if(postId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        PostResponse result = this.postService.getPostByPostId(postId);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<URI> requestCreatePost(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Post.Content body) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            PostCreateRequest requestDto = new PostCreateRequest(body);
            requestDto.setInternalUserId(id);

            try{
                URI relativeURI = this.postService.createPost(requestDto);
                return ResponseEntity.created(relativeURI).build();
            } catch (Exception e) {
                System.out.printf("Catch post error: " + e.getMessage());
                return ResponseEntity.internalServerError().build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> requestDeletePost(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("postId") String postId) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(postId == null) return ResponseEntity.badRequest().build();
        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            this.postService.deletePost(id, postId);
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> requestLikePost(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("postId") String postId) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(postId == null) return ResponseEntity.badRequest().build();
        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            this.postService.likePost(id, postId);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<?> requestUnlikePost(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("postId") String postId) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(postId == null) return ResponseEntity.badRequest().build();
        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            this.postService.unlikePost(id, postId);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/{postId}/scrap")
    public ResponseEntity<URI> requestScrapPost(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("postId") String postId) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(postId == null) return ResponseEntity.badRequest().build();
        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            URI relativeURI = this.postService.scrapPost(id, postId);
            return ResponseEntity.created(relativeURI).build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/{postId}/report")
    public ResponseEntity<?> requestReportPost(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("postId") String postId) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(postId == null) return ResponseEntity.badRequest().build();
        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            this.postService.reportPost(id, postId);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}