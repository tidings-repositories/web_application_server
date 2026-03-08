package com.delivalue.tidings.domain.search.controller;

import com.delivalue.tidings.domain.post.dto.PostResponse;
import com.delivalue.tidings.domain.profile.dto.ProfileResponse;
import com.delivalue.tidings.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

	private final SearchService searchService;

	@GetMapping("/user")
	public ResponseEntity<List<ProfileResponse>> requestSearchUser(
			@AuthenticationPrincipal String userId,
			@RequestParam(value = "q") String keyword
	) {
		if (keyword == null || keyword.length() < 2) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		List<ProfileResponse> result = this.searchService.getProfileBySearchKeyword(keyword);
		return ResponseEntity.ok(result);
	}

	@GetMapping("/post")
	public ResponseEntity<List<PostResponse>> requestSearchPost(
			@AuthenticationPrincipal String userId,
			@RequestParam(value = "q") String keyword
	) {
		if (keyword == null || keyword.length() < 2) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}

		List<PostResponse> result = this.searchService.getPostBySearchKeyword(keyword);
		return ResponseEntity.ok(result);
	}
}
