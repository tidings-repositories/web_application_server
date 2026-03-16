package com.delivalue.tidings.domain.block.controller;

import com.delivalue.tidings.domain.block.dto.BlockRequest;
import com.delivalue.tidings.domain.block.service.BlockService;
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
@RequestMapping("/block")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    @PostMapping
    public ResponseEntity<?> requestBlockMember(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody BlockRequest body
    ) {
        blockService.blockMember(userId, body.getPublicId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<?> requestUnblockMember(
            @AuthenticationPrincipal String userId,
            @PathVariable("publicId") String publicId
    ) {
        blockService.unblockMember(userId, publicId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<ProfileResponse>> requestBlockList(
            @AuthenticationPrincipal String userId
    ) {
        List<ProfileResponse> result = blockService.getBlockList(userId);
        return ResponseEntity.ok(result);
    }
}
