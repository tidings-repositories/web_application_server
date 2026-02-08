package com.delivalue.tidings.domain.auth.controller;

import com.delivalue.tidings.common.TokenProvider;
import com.delivalue.tidings.domain.auth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

	@Mock
	private AuthService authService;

	@Mock
	private TokenProvider tokenProvider;

	@InjectMocks
	private AuthController authController;

	@Nested
	@DisplayName("refresh - 토큰 갱신")
	class Refresh {

		@Test
		@DisplayName("유효한 REFRESH 토큰으로 새 ACCESS 토큰을 발급한다")
		void validRefreshToken_returnsNewAccessToken() {
			MockHttpServletRequest request = new MockHttpServletRequest();
			request.addHeader("Authorization", "Bearer valid.refresh.token");
			String userId = "google@123";

			when(tokenProvider.extractRefreshUserId("valid.refresh.token")).thenReturn(Optional.of(userId));
			when(tokenProvider.generateJWT(userId, "ACCESS")).thenReturn("new.access.token");

			var response = authController.refresh(request);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().getResult()).isEqualTo("refresh");
			assertThat(response.getBody().getAccessToken()).isEqualTo("new.access.token");
			assertThat(response.getBody().getRefreshToken()).isNull();
		}

		@Test
		@DisplayName("Authorization 헤더가 없으면 401 예외를 발생시킨다")
		void noAuthorizationHeader_throwsUnauthorized() {
			MockHttpServletRequest request = new MockHttpServletRequest();

			assertThatThrownBy(() -> authController.refresh(request))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> {
						ResponseStatusException rse = (ResponseStatusException) ex;
						assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
					});
		}

		@Test
		@DisplayName("Bearer 접두어가 없으면 401 예외를 발생시킨다")
		void noBearerPrefix_throwsUnauthorized() {
			MockHttpServletRequest request = new MockHttpServletRequest();
			request.addHeader("Authorization", "Basic some-credentials");

			assertThatThrownBy(() -> authController.refresh(request))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> {
						ResponseStatusException rse = (ResponseStatusException) ex;
						assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
					});
		}

		@Test
		@DisplayName("유효하지 않은 REFRESH 토큰이면 401 예외를 발생시킨다")
		void invalidRefreshToken_throwsUnauthorized() {
			MockHttpServletRequest request = new MockHttpServletRequest();
			request.addHeader("Authorization", "Bearer invalid.refresh.token");

			when(tokenProvider.extractRefreshUserId("invalid.refresh.token")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> authController.refresh(request))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> {
						ResponseStatusException rse = (ResponseStatusException) ex;
						assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
					});
		}

		@Test
		@DisplayName("ACCESS 토큰으로 refresh 시도하면 실패한다")
		void accessTokenForRefresh_throwsUnauthorized() {
			MockHttpServletRequest request = new MockHttpServletRequest();
			request.addHeader("Authorization", "Bearer access.token.here");

			when(tokenProvider.extractRefreshUserId("access.token.here")).thenReturn(Optional.empty());

			assertThatThrownBy(() -> authController.refresh(request))
					.isInstanceOf(ResponseStatusException.class)
					.satisfies(ex -> {
						ResponseStatusException rse = (ResponseStatusException) ex;
						assertThat(rse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
					});
		}
	}

	@Nested
	@DisplayName("delete - 계정 삭제")
	class Delete {

		@Test
		@DisplayName("인증된 사용자의 계정을 삭제한다")
		void authenticatedUser_deletesAccount() {
			String userId = "google@123";

			var response = authController.delete(userId);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			verify(authService).deleteMember(userId);
		}
	}

	@Nested
	@DisplayName("checkPublicId - Public ID 중복 확인")
	class CheckPublicId {

		@Test
		@DisplayName("Public ID를 공백 제거 후 검증한다")
		void trimmedPublicId_callsService() {
			var validateResponse = new com.delivalue.tidings.domain.auth.dto.PublicIdValidateResponse("testid");
			when(authService.checkPublicIdUsable("testid")).thenReturn(validateResponse);

			var response = authController.checkPublicId("  testid  ");

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
			verify(authService).checkPublicIdUsable("testid");
		}
	}
}
