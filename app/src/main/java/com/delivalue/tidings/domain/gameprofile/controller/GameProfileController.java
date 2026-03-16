package com.delivalue.tidings.domain.gameprofile.controller;

import com.delivalue.tidings.domain.gameprofile.dto.GameProfileRequest;
import com.delivalue.tidings.domain.gameprofile.dto.GameProfileResponse;
import com.delivalue.tidings.domain.gameprofile.service.GameProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/profile/game")
@RequiredArgsConstructor
public class GameProfileController {

    private final GameProfileService gameProfileService;

    @GetMapping
    public ResponseEntity<List<GameProfileResponse>> requestGameProfiles(
            @AuthenticationPrincipal String userId
    ) {
        List<GameProfileResponse> result = gameProfileService.getGameProfiles(userId);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<GameProfileResponse> requestAddGameProfile(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody GameProfileRequest body
    ) {
        GameProfileResponse result = gameProfileService.addGameProfile(userId, body);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{profileId}")
    public ResponseEntity<?> requestRemoveGameProfile(
            @AuthenticationPrincipal String userId,
            @PathVariable("profileId") Long profileId
    ) {
        gameProfileService.removeGameProfile(userId, profileId);
        return ResponseEntity.noContent().build();
    }
}
