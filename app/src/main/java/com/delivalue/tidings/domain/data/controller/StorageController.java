package com.delivalue.tidings.domain.data.controller;

import com.delivalue.tidings.common.RequestValidator;
import com.delivalue.tidings.domain.data.dto.PostMediaUploadRequest;
import com.delivalue.tidings.domain.data.dto.ProfileUploadRequest;
import com.delivalue.tidings.domain.data.service.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {

	private final StorageService storageService;
	private final RequestValidator requestValidator;

	@PostMapping("/api/upload/profile")
	public ResponseEntity<Map<String, String>> getProfileUploadUrl(
			@AuthenticationPrincipal String userId,
			@Valid @RequestBody ProfileUploadRequest body
	) {
		if (!requestValidator.checkImageContentType(body.getContentType())) {
			return ResponseEntity.badRequest().build();
		}

		URL presignedURL = storageService.getProfilePresignedUploadUrl(userId, body.getContentType());

		if (presignedURL != null) {
			return ResponseEntity.ok(Map.of("presignedUrl", presignedURL.toString()));
		} else {
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping("/api/upload/post")
	public ResponseEntity<Map<String, List<String>>> getPostMediaUploadUrls(
			@AuthenticationPrincipal String userId,
			@Valid @RequestBody PostMediaUploadRequest body
	) {
		List<String> result = new ArrayList<>();
		for (String contentType : body.getContentTypes()) {
			if (!requestValidator.checkMediaContentType(contentType)) {
				return ResponseEntity.badRequest().build();
			}

			URL presignedURL = storageService.getPostMediaPresignedUploadUrl(userId, contentType);
			result.add(presignedURL.toString());
		}

		return ResponseEntity.ok(Map.of("presignedUrls", result));
	}
}
