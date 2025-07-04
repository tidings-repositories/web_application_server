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

    @PostMapping("/{publicId}")
    public ResponseEntity<List<PostResponse>> requestUserPostList(@PathVariable("publicId") String publicId, @RequestBody Map<String, String> body) {
        OffsetDateTime requestCursor = body.get("createdAt") != null ? OffsetDateTime.parse((String) body.get("createdAt")) : null;
        if(publicId == null || requestCursor == null) return ResponseEntity.badRequest().build();
        LocalDateTime cursorTime = requestCursor.toLocalDateTime();

        System.out.printf("this time is: " + cursorTime.toString());

        try {
            List<PostResponse> result = this.postService.getUserPostByCursor(publicId, cursorTime);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.printf("Catch Error /post/{publicId}: " + e.getMessage());
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
    public ResponseEntity<?> requestCreatePost(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Post.Content body) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            PostCreateRequest requestDto = new PostCreateRequest(body);
            requestDto.setInternalUserId(id);

            try{
                this.postService.createPost(requestDto);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                System.out.printf("Catch post error: " + e.getMessage());
                return ResponseEntity.badRequest().build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

//    @DeleteMapping("/{postId}")
//    public void requestDeletePost(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("postId") String postId) {
//        //
//    }
//
//    @PostMapping("/{postId}/like")
//    public void requestLikePost(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("postId") String postId) {
//
//    }
//
//    @PostMapping("/{postId}/scrap")
//    public void requestScrapPost(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("postId") String postId) {
//
//    }
}