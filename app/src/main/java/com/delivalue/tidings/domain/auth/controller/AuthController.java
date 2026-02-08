package com.delivalue.tidings.domain.auth.controller;

import com.delivalue.tidings.common.TokenProvider;
import com.delivalue.tidings.domain.auth.dto.LoginResponse;
import com.delivalue.tidings.domain.auth.dto.PublicIdValidateResponse;
import com.delivalue.tidings.domain.auth.dto.RegisterPublicIdRequest;
import com.delivalue.tidings.domain.auth.dto.RegisterRequest;
import com.delivalue.tidings.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final TokenProvider tokenProvider;

	@GetMapping("/login")
	public ResponseEntity<LoginResponse> continueWithGoogle(
			OAuth2AuthenticationToken authToken,
			@AuthenticationPrincipal OAuth2User principal
	) {
		LoginResponse.LoginResponseBuilder response = LoginResponse.builder();
		Map<String, Object> resource = principal.getAttributes();

		String registrationId = authToken.getAuthorizedClientRegistrationId();
		String expectId = registrationId + "@" + resource.get("sub");

		boolean isUserExist = authService.checkUserExist(expectId);
		if (isUserExist) {
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
	public ResponseEntity<LoginResponse> register(
			OAuth2AuthenticationToken authToken,
			@AuthenticationPrincipal OAuth2User principal,
			@Valid @RequestBody RegisterPublicIdRequest body
	) {
		String publicId = body.getPublicId();
		PublicIdValidateResponse validateResult = authService.checkPublicIdUsable(publicId);
		Map<String, Object> resource = principal.getAttributes();

		if (validateResult.isResult() && resource != null) {
			Object name = resource.get("name");
			Object email = resource.get("email");
			if (name == null || email == null) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "필수 사용자 정보가 누락되었습니다.");
			}

			String registrationId = authToken.getAuthorizedClientRegistrationId();
			String internalId = registrationId + "@" + resource.get("sub");
			RegisterRequest newMemberData = new RegisterRequest(
					internalId,
					publicId,
					name.toString(),
					email.toString()
			);

			LoginResponse response = authService.registerMember(newMemberData);
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.badRequest().build();
		}
	}

	@GetMapping("/refresh")
	public ResponseEntity<LoginResponse> refresh(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		String token = bearerToken.substring(7);
		String userId = tokenProvider.extractRefreshUserId(token)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));

		LoginResponse response = LoginResponse.builder()
				.result("refresh")
				.refreshToken(null)
				.accessToken(this.tokenProvider.generateJWT(userId, "ACCESS"))
				.build();

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/account")
	public ResponseEntity<?> delete(@AuthenticationPrincipal String userId) {
		this.authService.deleteMember(userId);
		return ResponseEntity.ok().build();
	}
}
