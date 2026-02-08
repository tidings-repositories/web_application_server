package com.delivalue.tidings.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.lang.reflect.Constructor;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class TokenProviderTest {

	private static final String TEST_SECRET = Base64.getEncoder().encodeToString(
			"this-is-a-test-secret-key-that-is-long-enough-for-hmac-sha256-algorithm".getBytes()
	);
	private static final String TEST_USER_ID = "google@123456789";

	private TokenProvider tokenProvider;
	private SecretKey key;

	@BeforeEach
	void setUp() throws Exception {
		Constructor<TokenProvider> constructor = TokenProvider.class.getDeclaredConstructor(String.class);
		constructor.setAccessible(true);
		tokenProvider = constructor.newInstance(TEST_SECRET);

		byte[] keyBytes = Base64.getDecoder().decode(TEST_SECRET);
		key = Keys.hmacShaKeyFor(keyBytes);
	}

	@Nested
	@DisplayName("generateJWT")
	class GenerateJWT {

		@Test
		@DisplayName("ACCESS 토큰 생성 - 정상적으로 토큰을 생성한다")
		void generateAccessToken_success() {
			String token = tokenProvider.generateJWT(TEST_USER_ID, "ACCESS");

			assertThat(token).isNotNull().isNotEmpty();

			var claims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token)
					.getPayload();

			assertThat(claims.getSubject()).isEqualTo(TEST_USER_ID);
			assertThat(claims.get("type", String.class)).isEqualTo("ACCESS");
			assertThat(claims.getIssuedAt()).isNotNull();
			assertThat(claims.getExpiration()).isAfter(new Date());
		}

		@Test
		@DisplayName("REFRESH 토큰 생성 - ACCESS 토큰보다 만료 시간이 길다")
		void generateRefreshToken_longerExpiration() {
			String accessToken = tokenProvider.generateJWT(TEST_USER_ID, "ACCESS");
			String refreshToken = tokenProvider.generateJWT(TEST_USER_ID, "REFRESH");

			var accessClaims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(accessToken)
					.getPayload();

			var refreshClaims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(refreshToken)
					.getPayload();

			assertThat(refreshClaims.getExpiration()).isAfter(accessClaims.getExpiration());
			assertThat(refreshClaims.get("type", String.class)).isEqualTo("REFRESH");
		}

		@Test
		@DisplayName("ACCESS 토큰 만료 시간은 약 1시간이다")
		void accessTokenExpiration_isOneHour() {
			String token = tokenProvider.generateJWT(TEST_USER_ID, "ACCESS");

			var claims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token)
					.getPayload();

			long expirationDiffMs = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
			long oneHourMs = 3600 * 1000L;

			assertThat(expirationDiffMs).isBetween(oneHourMs - 1000L, oneHourMs + 1000L);
		}

		@Test
		@DisplayName("REFRESH 토큰 만료 시간은 약 28일이다")
		void refreshTokenExpiration_is28Days() {
			String token = tokenProvider.generateJWT(TEST_USER_ID, "REFRESH");

			var claims = Jwts.parser()
					.verifyWith(key)
					.build()
					.parseSignedClaims(token)
					.getPayload();

			long expirationDiffMs = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
			long twentyEightDaysMs = 3600L * 24 * 28 * 1000L;

			assertThat(expirationDiffMs).isBetween(twentyEightDaysMs - 1000L, twentyEightDaysMs + 1000L);
		}
	}

	@Nested
	@DisplayName("extractUserId")
	class ExtractUserId {

		@Test
		@DisplayName("유효한 ACCESS 토큰에서 userId를 추출한다")
		void extractUserId_validAccessToken_returnsUserId() {
			String token = tokenProvider.generateJWT(TEST_USER_ID, "ACCESS");

			Optional<String> result = tokenProvider.extractUserId(token);

			assertThat(result).isPresent().contains(TEST_USER_ID);
		}

		@Test
		@DisplayName("REFRESH 토큰으로 extractUserId 호출 시 빈 값을 반환한다")
		void extractUserId_refreshToken_returnsEmpty() {
			String token = tokenProvider.generateJWT(TEST_USER_ID, "REFRESH");

			Optional<String> result = tokenProvider.extractUserId(token);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("잘못된 토큰이면 빈 값을 반환한다")
		void extractUserId_invalidToken_returnsEmpty() {
			Optional<String> result = tokenProvider.extractUserId("invalid.token.here");

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("빈 문자열 토큰이면 빈 값을 반환한다")
		void extractUserId_emptyToken_returnsEmpty() {
			Optional<String> result = tokenProvider.extractUserId("");

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("만료된 토큰이면 빈 값을 반환한다")
		void extractUserId_expiredToken_returnsEmpty() {
			String expiredToken = Jwts.builder()
					.subject(TEST_USER_ID)
					.claim("type", "ACCESS")
					.issuedAt(new Date(System.currentTimeMillis() - 7200_000))
					.expiration(new Date(System.currentTimeMillis() - 3600_000))
					.signWith(key)
					.compact();

			Optional<String> result = tokenProvider.extractUserId(expiredToken);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("다른 키로 서명된 토큰이면 빈 값을 반환한다")
		void extractUserId_differentKey_returnsEmpty() {
			SecretKey differentKey = Keys.hmacShaKeyFor(
					"another-secret-key-that-is-also-long-enough-for-hmac-sha256-ok".getBytes()
			);
			String tokenWithDifferentKey = Jwts.builder()
					.subject(TEST_USER_ID)
					.claim("type", "ACCESS")
					.issuedAt(new Date())
					.expiration(new Date(System.currentTimeMillis() + 3600_000))
					.signWith(differentKey)
					.compact();

			Optional<String> result = tokenProvider.extractUserId(tokenWithDifferentKey);

			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("extractRefreshUserId")
	class ExtractRefreshUserId {

		@Test
		@DisplayName("유효한 REFRESH 토큰에서 userId를 추출한다")
		void extractRefreshUserId_validRefreshToken_returnsUserId() {
			String token = tokenProvider.generateJWT(TEST_USER_ID, "REFRESH");

			Optional<String> result = tokenProvider.extractRefreshUserId(token);

			assertThat(result).isPresent().contains(TEST_USER_ID);
		}

		@Test
		@DisplayName("ACCESS 토큰으로 extractRefreshUserId 호출 시 빈 값을 반환한다")
		void extractRefreshUserId_accessToken_returnsEmpty() {
			String token = tokenProvider.generateJWT(TEST_USER_ID, "ACCESS");

			Optional<String> result = tokenProvider.extractRefreshUserId(token);

			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("잘못된 토큰이면 빈 값을 반환한다")
		void extractRefreshUserId_invalidToken_returnsEmpty() {
			Optional<String> result = tokenProvider.extractRefreshUserId("not-a-valid-jwt");

			assertThat(result).isEmpty();
		}
	}

	@Nested
	@DisplayName("토큰 타입 교차 검증")
	class CrossTypeValidation {

		@Test
		@DisplayName("ACCESS 토큰과 REFRESH 토큰은 서로 교차 사용할 수 없다")
		void crossTypeValidation_tokensAreNotInterchangeable() {
			String accessToken = tokenProvider.generateJWT(TEST_USER_ID, "ACCESS");
			String refreshToken = tokenProvider.generateJWT(TEST_USER_ID, "REFRESH");

			assertThat(tokenProvider.extractUserId(accessToken)).isPresent();
			assertThat(tokenProvider.extractUserId(refreshToken)).isEmpty();

			assertThat(tokenProvider.extractRefreshUserId(refreshToken)).isPresent();
			assertThat(tokenProvider.extractRefreshUserId(accessToken)).isEmpty();
		}
	}
}
