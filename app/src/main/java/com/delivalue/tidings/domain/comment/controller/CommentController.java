package com.delivalue.tidings.domain.comment.controller;

import com.delivalue.tidings.common.TokenProvider;
import com.delivalue.tidings.domain.comment.dto.CommentCreateRequest;
import com.delivalue.tidings.domain.comment.dto.CommentResponse;
import com.delivalue.tidings.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final TokenProvider tokenProvider;

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponse>> requestGetPostComment(@PathVariable("postId") String postId) {
        if(postId == null) return ResponseEntity.badRequest().build();

        List<CommentResponse> result = this.commentService.getPostComment(postId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{postId}")
    public ResponseEntity<URI> requestCreateComment(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("postId") String postId,
            @RequestBody CommentCreateRequest body
            ) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(postId == null) return ResponseEntity.badRequest().build();
        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            URI result = this.commentService.addComment(id, postId, body);
            return ResponseEntity.created(result).build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PostMapping("/{postId}/{commentId}")
    public ResponseEntity<URI> requestCreateComment(
            @RequestHeader("Authorization") String authorizationHeader,
            @PathVariable("postId") String postId,
            @PathVariable("commentId") String commentId,
            @RequestBody CommentCreateRequest body
    ) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(postId == null || commentId == null) return ResponseEntity.badRequest().build();
        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            URI result = this.commentService.addReply(id, postId, commentId, body);
            return ResponseEntity.created(result).build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

//    @DeleteMapping("/{commentId}")
//    public ResponseEntity<?> requestDeleteComment(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("commentId") String commentId) {
//        int TOKEN_PREFIX_LENGTH = 7;
//
//        if(commentId == null) return ResponseEntity.badRequest().build();
//        if(authorizationHeader != null
//                && authorizationHeader.startsWith("Bearer ")
//                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
//            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));
//
//            this.commentService.deleteComment(id, commentId);
//            return ResponseEntity.noContent().build();
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//    }

//    @PostMapping("/{commentId}/report")
//    public ResponseEntity<?> requestReportComment(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("commentId") String commentId) {
//
//    }
}
