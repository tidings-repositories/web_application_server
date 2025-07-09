package com.delivalue.tidings.domain.search.controller;

import com.delivalue.tidings.common.TokenProvider;
import com.delivalue.tidings.domain.profile.dto.ProfileResponse;
import com.delivalue.tidings.domain.profile.service.ProfileService;
import com.delivalue.tidings.domain.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    private final TokenProvider tokenProvider;

    @GetMapping("/user")
    public ResponseEntity<List<ProfileResponse>> requestSearchUser(@RequestHeader("Authorization") String authorizationHeader, @RequestParam(value = "q") String keyword) {
        int TOKEN_PREFIX_LENGTH = 7;

        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            List<ProfileResponse> result = this.searchService.getProfileBySearchKeyword(keyword);

            return ResponseEntity.ok(result);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
