package com.delivalue.tidings.domain.comment.controller;

import com.delivalue.tidings.domain.comment.dto.CommentCreateRequest;
import com.delivalue.tidings.domain.comment.dto.CommentResponse;
import com.delivalue.tidings.domain.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@GetMapping("/{postId}")
	public ResponseEntity<List<CommentResponse>> requestGetPostComment(@PathVariable("postId") String postId) {
		List<CommentResponse> result = this.commentService.getPostComment(postId);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/{postId}")
	public ResponseEntity<URI> requestCreateComment(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId,
			@RequestBody CommentCreateRequest body
	) {
		URI result = this.commentService.addComment(userId, postId, body);
		return ResponseEntity.created(result).build();
	}

	@PostMapping("/{postId}/{commentId}")
	public ResponseEntity<URI> requestCreateComment(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId,
			@PathVariable("commentId") String commentId,
			@RequestBody CommentCreateRequest body
	) {
		URI result = this.commentService.addReply(userId, postId, commentId, body);
		return ResponseEntity.created(result).build();
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<?> requestDeleteComment(
			@AuthenticationPrincipal String userId,
			@PathVariable("commentId") String commentId
	) {
		this.commentService.deleteComment(userId, commentId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{commentId}/report")
	public ResponseEntity<?> requestReportComment(
			@AuthenticationPrincipal String userId,
			@PathVariable("commentId") String commentId
	) {
		this.commentService.reportComment(userId, commentId);
		return ResponseEntity.ok().build();
	}
}
