package com.delivalue.tidings.common.security;

import com.delivalue.tidings.common.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

	@Mock
	private TokenProvider tokenProvider;

	@InjectMocks
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Mock
	private FilterChain filterChain;

	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@BeforeEach
	void setUp() {
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
		SecurityContextHolder.clearContext();
	}

	@Nested
	@DisplayName("shouldNotFilter - 경로 제외 판단")
	class ShouldNotFilter {

		@Test
		@DisplayName("OAuth2 경로는 필터를 건너뛴다")
		void oauth2Path_shouldNotFilter() {
			request.setRequestURI("/oauth2/authorization/google");
			assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isTrue();
		}

		@Test
		@DisplayName("OAuth2 콜백 경로는 필터를 건너뛴다")
		void oauth2CallbackPath_shouldNotFilter() {
			request.setRequestURI("/login/oauth2/code/google");
			assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isTrue();
		}

		@Test
		@DisplayName("/auth/refresh 경로는 필터를 건너뛴다")
		void authRefreshPath_shouldNotFilter() {
			request.setRequestURI("/auth/refresh");
			assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isTrue();
		}

		@Test
		@DisplayName("일반 API 경로는 필터를 적용한다")
		void normalApiPath_shouldFilter() {
			request.setRequestURI("/post");
			assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isFalse();
		}

		@Test
		@DisplayName("/auth/login 경로는 필터를 적용한다 (제외 목록에 없음)")
		void authLoginPath_shouldFilter() {
			request.setRequestURI("/auth/login");
			assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isFalse();
		}

		@Test
		@DisplayName("/profile 경로는 필터를 적용한다")
		void profilePath_shouldFilter() {
			request.setRequestURI("/profile");
			assertThat(jwtAuthenticationFilter.shouldNotFilter(request)).isFalse();
		}
	}

	@Nested
	@DisplayName("doFilterInternal - 필터 실행 로직")
	class DoFilterInternal {

		@Test
		@DisplayName("유효한 Bearer 토큰이면 SecurityContext에 인증 정보를 설정한다")
		void validBearerToken_setsAuthentication() throws ServletException, IOException {
			String userId = "google@12345";
			String token = "valid.jwt.token";
			request.addHeader("Authorization", "Bearer " + token);

			when(tokenProvider.extractUserId(token)).thenReturn(Optional.of(userId));

			jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

			assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
			assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal()).isEqualTo(userId);
			verify(filterChain).doFilter(request, response);
		}

		@Test
		@DisplayName("Authorization 헤더가 없으면 SecurityContext를 설정하지 않는다")
		void noAuthorizationHeader_doesNotSetAuthentication() throws ServletException, IOException {
			jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

			assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
			verify(filterChain).doFilter(request, response);
			verify(tokenProvider, never()).extractUserId(anyString());
		}

		@Test
		@DisplayName("Bearer 접두어가 없는 Authorization 헤더면 SecurityContext를 설정하지 않는다")
		void noBearerPrefix_doesNotSetAuthentication() throws ServletException, IOException {
			request.addHeader("Authorization", "Basic some-credentials");

			jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

			assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
			verify(filterChain).doFilter(request, response);
			verify(tokenProvider, never()).extractUserId(anyString());
		}

		@Test
		@DisplayName("토큰이 유효하지 않으면 SecurityContext를 설정하지 않는다")
		void invalidToken_doesNotSetAuthentication() throws ServletException, IOException {
			String token = "invalid.jwt.token";
			request.addHeader("Authorization", "Bearer " + token);

			when(tokenProvider.extractUserId(token)).thenReturn(Optional.empty());

			jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

			assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
			verify(filterChain).doFilter(request, response);
		}

		@Test
		@DisplayName("토큰 검증 실패해도 filterChain은 반드시 호출된다")
		void tokenValidationFails_filterChainStillCalled() throws ServletException, IOException {
			request.addHeader("Authorization", "Bearer expired.token");

			when(tokenProvider.extractUserId("expired.token")).thenReturn(Optional.empty());

			jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

			verify(filterChain).doFilter(request, response);
		}

		@Test
		@DisplayName("빈 Bearer 토큰이면 SecurityContext를 설정하지 않는다")
		void emptyBearerToken_doesNotSetAuthentication() throws ServletException, IOException {
			request.addHeader("Authorization", "Bearer ");

			jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

			assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
			verify(filterChain).doFilter(request, response);
		}
	}
}
