package com.delivalue.tidings.domain.data.controller;

import com.delivalue.tidings.common.RequestValidator;
import com.delivalue.tidings.common.TokenProvider;
import com.delivalue.tidings.domain.data.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.Map;

@RestController
@RequestMapping("/storage")
@RequiredArgsConstructor
public class StorageController {
    private final StorageService storageService;
    private final TokenProvider tokenProvider;
    private final RequestValidator requestValidator;

    @PostMapping("/api/upload/profile")
    public ResponseEntity<Map<String, String>> getProfileUploadUrl(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Map<String, String> body) {
        int TOKEN_PREFIX_LENGTH = 7;
        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            String contentType = body.get("content-type");
            if(body.isEmpty() || contentType == null) return ResponseEntity.badRequest().build();
            if(!requestValidator.checkImageContentType(contentType)) return ResponseEntity.badRequest().build();

            URL presignedURL = storageService.getProfilePresignedUploadUrl(id, contentType);

            if(presignedURL != null) return ResponseEntity.ok(Map.of("presignedUrl", presignedURL.toString()));
            else return ResponseEntity.internalServerError().build();
        } else return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}