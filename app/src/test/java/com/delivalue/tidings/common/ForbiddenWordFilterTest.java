package com.delivalue.tidings.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class ForbiddenWordFilterTest {

	@ParameterizedTest
	@ValueSource(strings = {"admin", "root", "stellagram", "system", "null"})
	@DisplayName("기본 금지어 목록에 포함된 단어는 true를 반환한다")
	void defaultForbiddenWords_returnsTrue(String word) {
		assertThat(ForbiddenWordFilter.containsForbiddenWord(word)).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {"Admin", "ADMIN", "Root", "ROOT", "Stellagram", "STELLAGRAM"})
	@DisplayName("금지어는 대소문자를 구분하지 않는다")
	void caseInsensitive_returnsTrue(String word) {
		assertThat(ForbiddenWordFilter.containsForbiddenWord(word)).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {"testuser", "myprofile", "newuser", "user123"})
	@DisplayName("금지어가 아닌 단어는 false를 반환한다")
	void normalWords_returnsFalse(String word) {
		assertThat(ForbiddenWordFilter.containsForbiddenWord(word)).isFalse();
	}

	@Test
	@DisplayName("금지어를 포함하지만 정확히 일치하지 않는 경우 false를 반환한다")
	void partialMatch_returnsFalse() {
		assertThat(ForbiddenWordFilter.containsForbiddenWord("administrator")).isFalse();
		assertThat(ForbiddenWordFilter.containsForbiddenWord("rootuser")).isFalse();
	}

	@ParameterizedTest
	@ValueSource(strings = {"admin123", "_admin_", "myadmin", "123admin", "superroot", "root_user"})
	@DisplayName("금지어가 부분 문자열로 포함되어 있어도 정확히 일치하지 않으면 false를 반환한다")
	void containsButNotExact_returnsFalse(String word) {
		assertThat(ForbiddenWordFilter.containsForbiddenWord(word)).isFalse();
	}
}
