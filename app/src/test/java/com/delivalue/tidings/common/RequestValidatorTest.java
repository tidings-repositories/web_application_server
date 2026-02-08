package com.delivalue.tidings.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class RequestValidatorTest {

	private RequestValidator requestValidator;

	@BeforeEach
	void setUp() {
		requestValidator = new RequestValidator();
		ReflectionTestUtils.setField(requestValidator, "bucket", "test-bucket.s3.amazonaws.com");
	}

	@Nested
	@DisplayName("checkImageContentType - 이미지 콘텐츠 타입 검증")
	class CheckImageContentType {

		@ParameterizedTest
		@ValueSource(strings = {"image/jpeg", "image/png", "image/webp", "image/bmp"})
		@DisplayName("일반 이미지 타입은 유효하다")
		void validImageTypes_returnsTrue(String contentType) {
			assertThat(requestValidator.checkImageContentType(contentType)).isTrue();
		}

		@Test
		@DisplayName("GIF 이미지는 유효하지 않다")
		void gifImage_returnsFalse() {
			assertThat(requestValidator.checkImageContentType("image/gif")).isFalse();
		}

		@Test
		@DisplayName("비디오 타입은 유효하지 않다")
		void videoType_returnsFalse() {
			assertThat(requestValidator.checkImageContentType("video/mp4")).isFalse();
		}

		@Test
		@DisplayName("텍스트 타입은 유효하지 않다")
		void textType_returnsFalse() {
			assertThat(requestValidator.checkImageContentType("text/html")).isFalse();
		}
	}

	@Nested
	@DisplayName("checkMediaContentType - 미디어 콘텐츠 타입 검증")
	class CheckMediaContentType {

		@ParameterizedTest
		@ValueSource(strings = {"image/jpeg", "image/png", "image/gif", "image/webp"})
		@DisplayName("이미지 타입은 유효하다")
		void imageTypes_returnsTrue(String contentType) {
			assertThat(requestValidator.checkMediaContentType(contentType)).isTrue();
		}

		@ParameterizedTest
		@ValueSource(strings = {"video/mp4", "video/webm", "video/quicktime"})
		@DisplayName("비디오 타입은 유효하다")
		void videoTypes_returnsTrue(String contentType) {
			assertThat(requestValidator.checkMediaContentType(contentType)).isTrue();
		}

		@ParameterizedTest
		@ValueSource(strings = {"text/html", "application/json", "audio/mp3"})
		@DisplayName("이미지/비디오가 아닌 타입은 유효하지 않다")
		void otherTypes_returnsFalse(String contentType) {
			assertThat(requestValidator.checkMediaContentType(contentType)).isFalse();
		}
	}

	@Nested
	@DisplayName("checkProfileUpdateParameter - 프로필 업데이트 파라미터 검증")
	class CheckProfileUpdateParameter {

		@Test
		@DisplayName("모든 파라미터가 null이면 유효하다")
		void allNull_returnsTrue() {
			assertThat(requestValidator.checkProfileUpdateParameter(null, null, null)).isTrue();
		}

		@Test
		@DisplayName("이름이 12자 이하이면 유효하다")
		void nameWithin12Chars_returnsTrue() {
			assertThat(requestValidator.checkProfileUpdateParameter("TestUserName", null, null)).isTrue();
		}

		@Test
		@DisplayName("이름이 12자 초과이면 유효하지 않다")
		void nameOver12Chars_returnsFalse() {
			assertThat(requestValidator.checkProfileUpdateParameter("TooLongUserName", null, null)).isFalse();
		}

		@Test
		@DisplayName("바이오가 100자 이하이면 유효하다")
		void bioWithin100Chars_returnsTrue() {
			String bio = "A".repeat(100);
			assertThat(requestValidator.checkProfileUpdateParameter(null, bio, null)).isTrue();
		}

		@Test
		@DisplayName("바이오가 100자 초과이면 유효하지 않다")
		void bioOver100Chars_returnsFalse() {
			String bio = "A".repeat(101);
			assertThat(requestValidator.checkProfileUpdateParameter(null, bio, null)).isFalse();
		}

		@Test
		@DisplayName("프로필 이미지 URL이 올바른 버킷 도메인으로 시작하면 유효하다")
		void validProfileImageUrl_returnsTrue() {
			String url = "https://test-bucket.s3.amazonaws.com/profile/image.jpg";
			assertThat(requestValidator.checkProfileUpdateParameter(null, null, url)).isTrue();
		}

		@Test
		@DisplayName("프로필 이미지 URL이 잘못된 도메인이면 유효하지 않다")
		void invalidProfileImageUrl_returnsFalse() {
			String url = "https://malicious.example.com/image.jpg";
			assertThat(requestValidator.checkProfileUpdateParameter(null, null, url)).isFalse();
		}

		@Test
		@DisplayName("모든 파라미터가 유효한 값이면 유효하다")
		void allValidParams_returnsTrue() {
			String url = "https://test-bucket.s3.amazonaws.com/profile/photo.jpg";
			assertThat(requestValidator.checkProfileUpdateParameter("Name", "Bio text", url)).isTrue();
		}

		@Test
		@DisplayName("이름 길이가 정확히 12자이면 유효하다")
		void nameExactly12Chars_returnsTrue() {
			assertThat(requestValidator.checkProfileUpdateParameter("123456789012", null, null)).isTrue();
		}

		@Test
		@DisplayName("이름 길이가 정확히 13자이면 유효하지 않다")
		void nameExactly13Chars_returnsFalse() {
			assertThat(requestValidator.checkProfileUpdateParameter("1234567890123", null, null)).isFalse();
		}
	}
}
