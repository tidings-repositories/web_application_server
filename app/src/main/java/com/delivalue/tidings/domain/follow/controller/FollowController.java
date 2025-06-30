package com.delivalue.tidings.domain.follow.controller;

import com.delivalue.tidings.common.TokenProvider;
import com.delivalue.tidings.domain.follow.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/follow")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;
    private final TokenProvider tokenProvider;

    @PostMapping("/request")
    public ResponseEntity<?> requestFollowUser(@RequestHeader("Authorization") String authorizationHeader, @RequestBody Map<String, Object> body) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));
            String followingUserPublicId = (String) body.get("follow");
            if(followingUserPublicId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            try {
                this.followService.addFollowUser(id, followingUserPublicId);

                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/{publicId}")
    public ResponseEntity<?> removeFollowUser(@RequestHeader("Authorization") String authorizationHeader, @PathVariable("publicId") String followingUserPublicId) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            if(followingUserPublicId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

            try {
                this.followService.removeFollowUser(id, followingUserPublicId);

                return ResponseEntity.ok().build();
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
