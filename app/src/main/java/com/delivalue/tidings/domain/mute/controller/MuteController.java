package com.delivalue.tidings.domain.mute.controller;

import com.delivalue.tidings.domain.mute.dto.MuteRequest;
import com.delivalue.tidings.domain.mute.service.MuteService;
import com.delivalue.tidings.domain.profile.dto.ProfileResponse;
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
@RequestMapping("/mute")
@RequiredArgsConstructor
public class MuteController {

    private final MuteService muteService;

    @PostMapping
    public ResponseEntity<?> requestMuteMember(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody MuteRequest body
    ) {
        muteService.muteMember(userId, body.getPublicId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<?> requestUnmuteMember(
            @AuthenticationPrincipal String userId,
            @PathVariable("publicId") String publicId
    ) {
        muteService.unmuteMember(userId, publicId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProfileResponse>> requestMuteList(
            @AuthenticationPrincipal String userId
    ) {
        List<ProfileResponse> result = muteService.getMuteList(userId);
        return ResponseEntity.ok(result);
    }
}
