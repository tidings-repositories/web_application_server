package com.delivalue.tidings.domain.follow.controller;

import com.delivalue.tidings.domain.follow.dto.FollowRequest;
import com.delivalue.tidings.domain.follow.service.FollowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService;

	@PostMapping("/request")
	public ResponseEntity<?> requestFollowUser(
			@AuthenticationPrincipal String userId,
			@Valid @RequestBody FollowRequest body
	) {
		this.followService.addFollowUser(userId, body.getFollow());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{publicId}")
	public ResponseEntity<?> removeFollowUser(
			@AuthenticationPrincipal String userId,
			@PathVariable("publicId") String followingUserPublicId
	) {
		this.followService.removeFollowUser(userId, followingUserPublicId);
		return ResponseEntity.ok().build();
	}
}
