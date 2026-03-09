package com.delivalue.tidings.domain.post.controller;

import com.delivalue.tidings.domain.post.dto.CursorRequest;
import com.delivalue.tidings.domain.post.dto.PostContentRequest;
import com.delivalue.tidings.domain.post.dto.PostCreateRequest;
import com.delivalue.tidings.domain.post.dto.PostImpressionRequest;
import com.delivalue.tidings.domain.post.dto.PostResponse;
import com.delivalue.tidings.domain.post.service.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@PostMapping("/recent")
	public ResponseEntity<List<PostResponse>> requestRecentPostList(@RequestBody CursorRequest body) {
		LocalDateTime cursorTime = body.getCreatedAt() != null ? body.getCreatedAt().toLocalDateTime() : null;
		List<PostResponse> result = this.postService.getRecentPostByCursor(body.getPostId(), cursorTime);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/feed")
	public ResponseEntity<List<PostResponse>> requestFeedPostList(
			@AuthenticationPrincipal String userId,
			@RequestBody CursorRequest body
	) {
		LocalDateTime cursorTime = body.getCreatedAt() != null ? body.getCreatedAt().toLocalDateTime() : null;
		List<PostResponse> result = this.postService.getFeedPostByCursor(userId, body.getPostId(), cursorTime);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{postId}")
	public ResponseEntity<PostResponse> requestPost(@PathVariable("postId") String postId) {
		PostResponse result = this.postService.getPostByPostId(postId);
		return ResponseEntity.ok(result);
	}

	@PostMapping
	public ResponseEntity<URI> requestCreatePost(
			@AuthenticationPrincipal String userId,
			@Valid @RequestBody PostContentRequest body
	) {
		PostCreateRequest requestDto = new PostCreateRequest(body);
		requestDto.setInternalUserId(userId);

		URI relativeURI = this.postService.createPost(requestDto);
		return ResponseEntity.created(relativeURI).build();
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity<?> requestDeletePost(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId
	) {
		this.postService.deletePost(userId, postId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{postId}/like")
	public ResponseEntity<?> requestLikePost(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId
	) {
		this.postService.likePost(userId, postId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{postId}/like")
	public ResponseEntity<?> requestUnlikePost(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId
	) {
		this.postService.unlikePost(userId, postId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{postId}/scrap")
	public ResponseEntity<URI> requestScrapPost(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId
	) {
		URI relativeURI = this.postService.scrapPost(userId, postId);
		return ResponseEntity.created(relativeURI).build();
	}

	@PostMapping("/{postId}/report")
	public ResponseEntity<?> requestReportPost(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId
	) {
		this.postService.reportPost(userId, postId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{postId}/bookmark")
	public ResponseEntity<?> requestBookmarkPost(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId
	) {
		this.postService.bookmarkPost(userId, postId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{postId}/bookmark")
	public ResponseEntity<?> requestUnbookmarkPost(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId
	) {
		this.postService.unbookmarkPost(userId, postId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{postId}/impression")
	public ResponseEntity<?> requestRecordImpression(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId,
			@RequestBody PostImpressionRequest body
	) {
		this.postService.recordImpression(userId, postId, body);
		return ResponseEntity.ok().build();
	}
}
