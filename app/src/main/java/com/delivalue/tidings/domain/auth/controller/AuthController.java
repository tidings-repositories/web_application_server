package com.delivalue.tidings.domain.auth.controller;

import com.delivalue.tidings.common.TokenProvider;
import com.delivalue.tidings.domain.auth.dto.LoginResponse;
import com.delivalue.tidings.domain.auth.dto.PublicIdValidateResponse;
import com.delivalue.tidings.domain.auth.dto.RegisterRequest;
import com.delivalue.tidings.domain.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final TokenProvider tokenProvider;

    public AuthController(AuthService authService, TokenProvider tokenProvider) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/login")
    public ResponseEntity<LoginResponse> continueWithGoogle(OAuth2AuthenticationToken authToken, @AuthenticationPrincipal OAuth2User principal) {
        LoginResponse.LoginResponseBuilder response = LoginResponse.builder();
        Map<String, Object> resource = principal.getAttributes();

        String registrationId = authToken.getAuthorizedClientRegistrationId();
        String expectId = registrationId + "@" + resource.get("sub");

        boolean isUserExist = authService.checkUserExist(expectId);
        if(isUserExist) {
            response
                    .result("login")
                    .refreshToken(this.tokenProvider.generateJWT(expectId, "REFRESH"))
                    .accessToken(this.tokenProvider.generateJWT(expectId, "ACCESS"));

            return ResponseEntity.ok(response.build());
        } else {
            response
                    .result("register")
                    .refreshToken(null)
                    .accessToken(null);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.build());
        }
    }

    @GetMapping("/check")
    public ResponseEntity<PublicIdValidateResponse> checkPublicId(@RequestParam(value = "id") String publicId) {
        return ResponseEntity.ok(authService.checkPublicIdUsable(publicId.trim()));
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(OAuth2AuthenticationToken authToken, @AuthenticationPrincipal OAuth2User principal, @RequestBody Map<String, String> body) {
        String publicId = body.get("publicId");
        PublicIdValidateResponse validateResult = authService.checkPublicIdUsable(publicId);
        Map<String, Object> resource = principal.getAttributes();

        if(validateResult.isResult() && resource != null) {
            LoginResponse response;
            String registrationId = authToken.getAuthorizedClientRegistrationId();
            String internalId = registrationId + "@" + resource.get("sub");
            RegisterRequest newMemberData = new RegisterRequest(internalId, publicId, resource.get("name").toString(), resource.get("email").toString());

            try {
                 response = authService.registerMember(newMemberData);
            } catch (Exception e) {
                return ResponseEntity.internalServerError().build();
            }

            return ResponseEntity.ok(response);
        }
        else return ResponseEntity.badRequest().build();
    }

    @GetMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestHeader("Authorization") String authorizationHeader) {
        int TOKEN_PREFIX_LENGTH = 7;
        LoginResponse.LoginResponseBuilder response = LoginResponse.builder();

        if(authorizationHeader != null
                && authorizationHeader.startsWith("Bearer ")
                && this.tokenProvider.validate(authorizationHeader.substring(TOKEN_PREFIX_LENGTH))) {
            String id = this.tokenProvider.getUserId(authorizationHeader.substring(TOKEN_PREFIX_LENGTH));

            response
                    .result("refresh")
                    .refreshToken(null)
                    .accessToken(this.tokenProvider.generateJWT(id, "ACCESS"));

            return ResponseEntity.ok(response.build());
        } else {
            response
                    .result("failed")
                    .refreshToken(null)
                    .accessToken(null);

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response.build());
        }
    }
}