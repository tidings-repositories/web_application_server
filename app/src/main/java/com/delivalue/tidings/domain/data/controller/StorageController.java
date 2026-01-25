package com.delivalue.tidings.domain.data.controller;

import com.delivalue.tidings.common.RequestValidator;
import com.delivalue.tidings.domain.data.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
			@RequestBody Map<String, String> body
	) {
		String contentType = body.get("content-type");
		if (body.isEmpty() || contentType == null) {
			return ResponseEntity.badRequest().build();
		}
		if (!requestValidator.checkImageContentType(contentType)) {
			return ResponseEntity.badRequest().build();
		}

		URL presignedURL = storageService.getProfilePresignedUploadUrl(userId, contentType);

		if (presignedURL != null) {
			return ResponseEntity.ok(Map.of("presignedUrl", presignedURL.toString()));
		} else {
			return ResponseEntity.internalServerError().build();
		}
	}

	@PostMapping("/api/upload/post")
	public ResponseEntity<Map<String, List<String>>> getPostMediaUploadUrls(
			@AuthenticationPrincipal String userId,
			@RequestBody Map<String, List<String>> body
	) {
		try {
			List<String> contentTypeList = body.get("content-types");
			if (body.isEmpty() || contentTypeList.isEmpty()) {
				return ResponseEntity.badRequest().build();
			}

			List<String> result = new ArrayList<>();
			for (String contentType : contentTypeList) {
				if (!requestValidator.checkMediaContentType(contentType)) {
					return ResponseEntity.badRequest().build();
				}

				URL presignedURL = storageService.getPostMediaPresignedUploadUrl(userId, contentType);
				result.add(presignedURL.toString());
			}

			return ResponseEntity.ok(Map.of("presignedUrls", result));
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}
}
