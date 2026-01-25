package com.delivalue.tidings.domain.follow.controller;

import com.delivalue.tidings.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {

	private final FollowService followService;

	@PostMapping("/request")
	public ResponseEntity<?> requestFollowUser(
			@AuthenticationPrincipal String userId,
			@RequestBody Map<String, Object> body
	) {
		String followingUserPublicId = (String) body.get("follow");
		if (followingUserPublicId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		try {
			this.followService.addFollowUser(userId, followingUserPublicId);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}

	@DeleteMapping("/{publicId}")
	public ResponseEntity<?> removeFollowUser(
			@AuthenticationPrincipal String userId,
			@PathVariable("publicId") String followingUserPublicId
	) {
		if (followingUserPublicId == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		try {
			this.followService.removeFollowUser(userId, followingUserPublicId);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			return ResponseEntity.internalServerError().build();
		}
	}
}
