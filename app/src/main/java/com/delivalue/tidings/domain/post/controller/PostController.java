package com.delivalue.tidings.domain.post.controller;

import com.delivalue.tidings.domain.data.entity.Post;
import com.delivalue.tidings.domain.post.dto.PostCreateRequest;
import com.delivalue.tidings.domain.post.dto.PostResponse;
import com.delivalue.tidings.domain.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

	@PostMapping("/recent")
	public ResponseEntity<List<PostResponse>> requestRecentPostList(@RequestBody Map<String, String> body) {
		String cursorId = body.get("postId");
		OffsetDateTime requestCursor = body.get("createdAt") != null ? OffsetDateTime.parse(body.get("createdAt")) : null;
		LocalDateTime cursorTime = requestCursor != null ? requestCursor.toLocalDateTime() : null;

		try {
			List<PostResponse> result = this.postService.getRecentPostByCursor(cursorId, cursorTime);
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			System.out.printf("Catch Error /post/recent: " + e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping("/feed")
	public ResponseEntity<List<PostResponse>> requestFeedPostList(
			@AuthenticationPrincipal String userId,
			@RequestBody Map<String, String> body
	) {
		String cursorId = body.get("postId");
		OffsetDateTime requestCursor = body.get("createdAt") != null ? OffsetDateTime.parse(body.get("createdAt")) : null;
		LocalDateTime cursorTime = requestCursor != null ? requestCursor.toLocalDateTime() : null;

		List<PostResponse> result = this.postService.getFeedPostByCursor(userId, cursorId, cursorTime);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/{postId}")
	public ResponseEntity<PostResponse> requestPost(@PathVariable("postId") String postId) {
		if (postId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		PostResponse result = this.postService.getPostByPostId(postId);
		return ResponseEntity.ok(result);
	}

	@PostMapping
	public ResponseEntity<URI> requestCreatePost(
			@AuthenticationPrincipal String userId,
			@RequestBody Post.Content body
	) {
		PostCreateRequest requestDto = new PostCreateRequest(body);
		requestDto.setInternalUserId(userId);

		try {
			URI relativeURI = this.postService.createPost(requestDto);
			return ResponseEntity.created(relativeURI).build();
		} catch (Exception e) {
			System.out.printf("Catch post error: " + e.getMessage());
			return ResponseEntity.internalServerError().build();
		}
	}

	@DeleteMapping("/{postId}")
	public ResponseEntity<?> requestDeletePost(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId
	) {
		if (postId == null) {
			return ResponseEntity.badRequest().build();
		}

		this.postService.deletePost(userId, postId);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{postId}/like")
	public ResponseEntity<?> requestLikePost(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId
	) {
		if (postId == null) {
			return ResponseEntity.badRequest().build();
		}

		this.postService.likePost(userId, postId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{postId}/like")
	public ResponseEntity<?> requestUnlikePost(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId
	) {
		if (postId == null) {
			return ResponseEntity.badRequest().build();
		}

		this.postService.unlikePost(userId, postId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/{postId}/scrap")
	public ResponseEntity<URI> requestScrapPost(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId
	) {
		if (postId == null) {
			return ResponseEntity.badRequest().build();
		}

		URI relativeURI = this.postService.scrapPost(userId, postId);
		return ResponseEntity.created(relativeURI).build();
	}

	@PostMapping("/{postId}/report")
	public ResponseEntity<?> requestReportPost(
			@AuthenticationPrincipal String userId,
			@PathVariable("postId") String postId
	) {
		if (postId == null) {
			return ResponseEntity.badRequest().build();
		}

		this.postService.reportPost(userId, postId);
		return ResponseEntity.ok().build();
	}
}
