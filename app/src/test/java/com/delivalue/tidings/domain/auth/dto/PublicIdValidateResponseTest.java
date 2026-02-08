package com.delivalue.tidings.domain.auth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PublicIdValidateResponseTest {

	@Nested
	@DisplayName("길이 검증")
	class LengthValidation {

		@Test
		@DisplayName("4자 미만이면 유효하지 않다 - overRangeString")
		void tooShort_returnsOverRangeString() {
			PublicIdValidateResponse response = new PublicIdValidateResponse("abc");

			assertThat(response.isResult()).isFalse();
			assertThat(response.getStatusMessage()).isEqualTo("overRangeString");
		}

		@Test
		@DisplayName("15자 초과이면 유효하지 않다 - overRangeString")
		void tooLong_returnsOverRangeString() {
			PublicIdValidateResponse response = new PublicIdValidateResponse("abcdefghijklmnop");

			assertThat(response.isResult()).isFalse();
			assertThat(response.getStatusMessage()).isEqualTo("overRangeString");
		}

		@Test
		@DisplayName("4자는 유효하다")
		void exactlyFour_isValid() {
			PublicIdValidateResponse response = new PublicIdValidateResponse("abcd");

			assertThat(response.isResult()).isTrue();
			assertThat(response.getStatusMessage()).isEqualTo("enableId");
		}

		@Test
		@DisplayName("15자는 유효하다")
		void exactlyFifteen_isValid() {
			PublicIdValidateResponse response = new PublicIdValidateResponse("abcdefghijklmno");

			assertThat(response.isResult()).isTrue();
			assertThat(response.getStatusMessage()).isEqualTo("enableId");
		}
	}

	@Nested
	@DisplayName("문자 검증")
	class CharacterValidation {

		@ParameterizedTest
		@ValueSource(strings = {"user_name", "User123", "test_user_1", "ALLCAPS", "abc_DEF_123"})
		@DisplayName("영문, 숫자, 언더스코어 조합은 유효하다")
		void validCharacters_isValid(String publicId) {
			PublicIdValidateResponse response = new PublicIdValidateResponse(publicId);

			assertThat(response.isResult()).isTrue();
			assertThat(response.getStatusMessage()).isEqualTo("enableId");
		}

		@ParameterizedTest
		@ValueSource(strings = {"user-name", "user.name", "user name", "user@name", "user#name"})
		@DisplayName("특수문자가 포함되면 유효하지 않다 - noSupportCharacter")
		void invalidCharacters_returnsNoSupportCharacter(String publicId) {
			PublicIdValidateResponse response = new PublicIdValidateResponse(publicId);

			assertThat(response.isResult()).isFalse();
			assertThat(response.getStatusMessage()).isEqualTo("noSupportCharacter");
		}

		@Test
		@DisplayName("한글이 포함되면 유효하지 않다")
		void koreanCharacters_returnsNoSupportCharacter() {
			PublicIdValidateResponse response = new PublicIdValidateResponse("user이름");

			assertThat(response.isResult()).isFalse();
			assertThat(response.getStatusMessage()).isEqualTo("noSupportCharacter");
		}
	}

	@Nested
	@DisplayName("길이와 문자 모두 위반하는 경우")
	class BothViolations {

		@Test
		@DisplayName("3자이면서 특수문자가 포함되면 noSupportCharacter를 반환한다")
		void shortAndInvalidChars_returnsNoSupportCharacter() {
			PublicIdValidateResponse response = new PublicIdValidateResponse("a-b");

			assertThat(response.isResult()).isFalse();
			// 길이 검증이 먼저 수행되지만 문자 검증이 나중에 덮어씀
			assertThat(response.getStatusMessage()).isEqualTo("noSupportCharacter");
		}

		@Test
		@DisplayName("3자이면서 유효한 문자만 있으면 overRangeString을 반환한다")
		void shortButValidChars_returnsOverRangeString() {
			PublicIdValidateResponse response = new PublicIdValidateResponse("abc");

			assertThat(response.isResult()).isFalse();
			assertThat(response.getStatusMessage()).isEqualTo("overRangeString");
		}
	}

	@Nested
	@DisplayName("정상 케이스")
	class ValidCases {

		@Test
		@DisplayName("숫자로만 구성된 ID도 유효하다")
		void numericOnly_isValid() {
			PublicIdValidateResponse response = new PublicIdValidateResponse("1234");

			assertThat(response.isResult()).isTrue();
		}

		@Test
		@DisplayName("언더스코어로만 구성된 ID도 유효하다")
		void underscoreOnly_isValid() {
			PublicIdValidateResponse response = new PublicIdValidateResponse("____");

			assertThat(response.isResult()).isTrue();
		}
	}
}
