package com.delivalue.tidings.domain.profile.controller;

import com.delivalue.tidings.common.RequestValidator;
import com.delivalue.tidings.domain.comment.dto.CommentResponse;
import com.delivalue.tidings.domain.comment.service.CommentService;
import com.delivalue.tidings.domain.follow.service.FollowService;
import com.delivalue.tidings.domain.post.dto.CursorRequest;
import com.delivalue.tidings.domain.post.dto.PostResponse;
import com.delivalue.tidings.domain.post.service.PostService;
import com.delivalue.tidings.domain.profile.dto.BadgeListResponse;
import com.delivalue.tidings.domain.profile.dto.ProfileResponse;
import com.delivalue.tidings.domain.profile.dto.ProfileUpdateBody;
import com.delivalue.tidings.domain.profile.dto.ProfileUpdateRequest;
import com.delivalue.tidings.domain.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

	private final ProfileService profileService;
	private final FollowService followService;
	private final PostService postService;
	private final CommentService commentService;
	private final RequestValidator requestValidator;

	@GetMapping
	public ResponseEntity<ProfileResponse> requestMyProfile(@AuthenticationPrincipal String userId) {
		ProfileResponse response = this.profileService.getProfileById(userId);
		return ResponseEntity.ok(response);
	}

	@PatchMapping
	public ResponseEntity<?> requestUpdateMyProfile(
			@AuthenticationPrincipal String userId,
			@RequestBody ProfileUpdateBody body
	) {
		boolean isValid = requestValidator.checkProfileUpdateParameter(
				body.getUserName(), body.getBio(), body.getProfileImage()
		);
		if (!isValid) {
			return ResponseEntity.badRequest().build();
		}

		ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest(
				userId, body.getUserName(), body.getBio(), body.getProfileImage(), body.getBadge()
		);
		this.profileService.updateProfile(profileUpdateRequest);

		return ResponseEntity.ok().build();
	}

	@GetMapping("/badge")
	public ResponseEntity<BadgeListResponse> requestMyBadgeList(@AuthenticationPrincipal String userId) {
		BadgeListResponse badgeListResponse = this.profileService.getBadgeListById(userId);
		return ResponseEntity.ok(badgeListResponse);
	}

	@GetMapping("/{publicId}")
	public ResponseEntity<ProfileResponse> requestProfile(@PathVariable("publicId") String publicId) {
		ProfileResponse response = this.profileService.getProfileByPublicId(publicId);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{publicId}/followings")
	public ResponseEntity<List<ProfileResponse>> requestFollowingList(@PathVariable("publicId") String publicId) {
		List<ProfileResponse> followingList = this.followService.getFollowingList(publicId);
		return ResponseEntity.ok(followingList);
	}

	@GetMapping("/{publicId}/followers")
	public ResponseEntity<List<ProfileResponse>> requestFollowerList(@PathVariable("publicId") String publicId) {
		List<ProfileResponse> followingList = this.followService.getFollowerList(publicId);
		return ResponseEntity.ok(followingList);
	}

	@PostMapping("/{publicId}/posts")
	public ResponseEntity<List<PostResponse>> requestUserPostList(
			@PathVariable("publicId") String publicId,
			@RequestBody CursorRequest body
	) {
		if (body.getCreatedAt() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		LocalDateTime cursorTime = body.getCreatedAt().toLocalDateTime();

		List<PostResponse> result = this.postService.getUserPostByCursor(publicId, cursorTime);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/{publicId}/comments")
	public ResponseEntity<List<CommentResponse>> requestUserCommentList(
			@PathVariable("publicId") String publicId,
			@RequestBody CursorRequest body
	) {
		if (body.getCreatedAt() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		LocalDateTime cursorTime = body.getCreatedAt().toLocalDateTime();

		List<CommentResponse> result = this.commentService.getUserCommentByCursor(publicId, cursorTime);
		return ResponseEntity.ok(result);
	}

	@PostMapping("/{publicId}/likes")
	public ResponseEntity<List<PostResponse>> requestUserLikePost(
			@PathVariable("publicId") String publicId,
			@RequestBody CursorRequest body
	) {
		if (body.getCreatedAt() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		LocalDateTime cursorTime = body.getCreatedAt().toLocalDateTime();

		List<PostResponse> result = this.postService.getUserLikePostByCursor(publicId, cursorTime, body.getPostId());
		return ResponseEntity.ok(result);
	}
}
