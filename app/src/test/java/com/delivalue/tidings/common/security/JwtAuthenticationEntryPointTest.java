package com.delivalue.tidings.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationEntryPointTest {

	private JwtAuthenticationEntryPoint entryPoint;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;
	private final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeEach
	void setUp() {
		entryPoint = new JwtAuthenticationEntryPoint();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Test
	@DisplayName("인증 실패 시 401 상태 코드와 JSON 응답을 반환한다")
	void commence_returns401WithJsonBody() throws IOException {
		AuthenticationException authException = new BadCredentialsException("Bad credentials");

		entryPoint.commence(request, response, authException);

		assertThat(response.getStatus()).isEqualTo(401);
		assertThat(response.getContentType()).startsWith("application/json");
		assertThat(response.getCharacterEncoding()).isEqualTo("UTF-8");

		@SuppressWarnings("unchecked")
		Map<String, Object> body = objectMapper.readValue(
				response.getContentAsByteArray(), Map.class
		);
		assertThat(body.get("status")).isEqualTo(401);
		assertThat(body.get("error")).isEqualTo("Unauthorized");
		assertThat(body.get("message")).isEqualTo("유효한 인증 토큰이 필요합니다.");
	}

	@Test
	@DisplayName("InsufficientAuthenticationException에도 동일한 401 응답을 반환한다")
	void insufficientAuth_returns401() throws IOException {
		AuthenticationException authException =
				new InsufficientAuthenticationException("Full authentication required");

		entryPoint.commence(request, response, authException);

		assertThat(response.getStatus()).isEqualTo(401);
		assertThat(response.getContentType()).startsWith("application/json");

		@SuppressWarnings("unchecked")
		Map<String, Object> body = objectMapper.readValue(
				response.getContentAsByteArray(), Map.class
		);
		assertThat(body.get("status")).isEqualTo(401);
		assertThat(body.get("error")).isEqualTo("Unauthorized");
	}
}
