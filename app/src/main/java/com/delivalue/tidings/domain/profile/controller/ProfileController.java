package com.delivalue.tidings.domain.profile.controller;

import com.delivalue.tidings.common.RequestValidator;
import com.delivalue.tidings.common.TokenProvider;
import com.delivalue.tidings.domain.data.repository.FollowRepository;
import com.delivalue.tidings.domain.follow.service.FollowService;
import com.delivalue.tidings.domain.post.dto.PostResponse;
import com.delivalue.tidings.domain.post.service.PostService;
import com.delivalue.tidings.domain.profile.dto.BadgeListResponse;
import com.delivalue.tidings.domain.profile.dto.ProfileResponse;
import com.delivalue.tidings.domain.profile.dto.ProfileUpdateRequest;
import com.delivalue.tidings.domain.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;
    private final FollowService followService;
    private final PostService postService;
    private final TokenProvider tokenProvider;
    private final RequestValidator requestValidator;

    @GetMapping
    public ResponseEntity<ProfileResponse> requestMyProfile(@RequestHeader("Authorization") String authorizationHeader) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            ProfileResponse response = this.profileService.getProfileById(id);

            if(response != null) return ResponseEntity.ok(response);
            else return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @PatchMapping
    public ResponseEntity<?> requestUpdateMyProfile(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody Map<String, Object> body
    ) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));
            String name = (String) body.get("user_name");
            String bio = (String) body.get("bio");
            String profileImage = (String) body.get("profile_image");
            Integer badgeId = (Integer) body.get("badge");

            boolean isValid = requestValidator.checkProfileUpdateParameter(name, bio, profileImage);
            if(!isValid) return ResponseEntity.badRequest().build();

            try {
                ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest(id, name, bio, profileImage, badgeId);
                this.profileService.updateProfile(profileUpdateRequest);

                return ResponseEntity.ok().build();
            } catch (Exception e) {
                System.out.printf("profile update catch: " + e);
                return ResponseEntity.internalServerError().build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/badge")
    public ResponseEntity<BadgeListResponse> requestMyBadgeList(@RequestHeader("Authorization") String authorizationHeader) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            try {
                BadgeListResponse badgeListResponse = this.profileService.getBadgeListById(id);

                return ResponseEntity.ok(badgeListResponse);
            } catch (Exception e) {
                System.out.printf("badge get catch: " + e);
                return ResponseEntity.internalServerError().build();
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @GetMapping("/{publicId}")
    public ResponseEntity<ProfileResponse> requestProfile(@PathVariable("publicId") String publicId) {
        ProfileResponse response = this.profileService.getProfileByPublicId(publicId);

        if(response != null) {
            return ResponseEntity.ok(response);
        } else return ResponseEntity.notFound().build();
    }

    @GetMapping("/{publicId}/followings")
    public ResponseEntity<List<ProfileResponse>> requestFollowingList(@PathVariable("publicId") String publicId) {
        try {
            List<ProfileResponse> followingList = this.followService.getFollowingList(publicId);
            return ResponseEntity.ok(followingList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{publicId}/followers")
    public ResponseEntity<List<ProfileResponse>> requestFollowerList(@PathVariable("publicId") String publicId) {
        try {
            List<ProfileResponse> followingList = this.followService.getFollowerList(publicId);
            return ResponseEntity.ok(followingList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{publicId}/posts")
    public ResponseEntity<List<PostResponse>> requestUserPostList(@PathVariable("publicId") String publicId, @RequestBody Map<String, String> body) {
        OffsetDateTime requestCursor = body.get("createdAt") != null ? OffsetDateTime.parse((String) body.get("createdAt")) : null;
        if(publicId == null || requestCursor == null) return ResponseEntity.badRequest().build();
        LocalDateTime cursorTime = requestCursor.toLocalDateTime();

        System.out.printf("this time is: " + cursorTime.toString());

        try {
            List<PostResponse> result = this.postService.getUserPostByCursor(publicId, cursorTime);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            System.out.printf("Catch Error /post/{publicId}: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/{publicId}/likes")
    public ResponseEntity<List<PostResponse>> requestUserLikePost(@PathVariable("publicId") String publicId, @RequestBody Map<String, String> body) {
        OffsetDateTime requestCursor = body.get("createdAt") != null ? OffsetDateTime.parse((String) body.get("createdAt")) : null;
        if(publicId == null || requestCursor == null) return ResponseEntity.badRequest().build();
        LocalDateTime cursorTime = requestCursor.toLocalDateTime();
        String cursorId = body.get("postId");

        List<PostResponse> result = this.postService.getUserLikePostByCursor(publicId, cursorTime, cursorId);
        return ResponseEntity.ok(result);
    }
}
