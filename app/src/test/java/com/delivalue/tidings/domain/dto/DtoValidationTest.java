package com.delivalue.tidings.domain.dto;

import com.delivalue.tidings.domain.auth.dto.RegisterPublicIdRequest;
import com.delivalue.tidings.domain.coupon.dto.CouponUseRequest;
import com.delivalue.tidings.domain.data.dto.PostMediaUploadRequest;
import com.delivalue.tidings.domain.data.dto.ProfileUploadRequest;
import com.delivalue.tidings.domain.follow.dto.FollowRequest;
import com.delivalue.tidings.domain.post.dto.PostContentRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DtoValidationTest {

	private static Validator validator;
	private static final ObjectMapper objectMapper = new ObjectMapper();

	@BeforeAll
	static void setUpValidator() {
		validator = Validation.buildDefaultValidatorFactory().getValidator();
	}

	private <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
		return objectMapper.convertValue(map, clazz);
	}

	@Nested
	@DisplayName("FollowRequest validation")
	class FollowRequestValidation {

		@Test
		@DisplayName("follow가 null이면 validation 실패")
		void nullFollow_fails() {
			FollowRequest request = fromMap(Map.of(), FollowRequest.class);
			Set<ConstraintViolation<FollowRequest>> violations = validator.validate(request);
			assertThat(violations).isNotEmpty();
			assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("follow"));
		}

		@Test
		@DisplayName("follow가 빈 문자열이면 validation 실패")
		void emptyFollow_fails() {
			FollowRequest request = fromMap(Map.of("follow", ""), FollowRequest.class);
			Set<ConstraintViolation<FollowRequest>> violations = validator.validate(request);
			assertThat(violations).isNotEmpty();
		}

		@Test
		@DisplayName("follow가 공백만 있으면 validation 실패")
		void blankFollow_fails() {
			FollowRequest request = fromMap(Map.of("follow", "   "), FollowRequest.class);
			Set<ConstraintViolation<FollowRequest>> violations = validator.validate(request);
			assertThat(violations).isNotEmpty();
		}

		@Test
		@DisplayName("follow가 유효한 값이면 validation 통과")
		void validFollow_passes() {
			FollowRequest request = fromMap(Map.of("follow", "testuser"), FollowRequest.class);
			Set<ConstraintViolation<FollowRequest>> violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}
	}

	@Nested
	@DisplayName("CouponUseRequest validation")
	class CouponUseRequestValidation {

		@Test
		@DisplayName("coupon이 null이면 validation 실패")
		void nullCoupon_fails() {
			CouponUseRequest request = fromMap(Map.of(), CouponUseRequest.class);
			Set<ConstraintViolation<CouponUseRequest>> violations = validator.validate(request);
			assertThat(violations).isNotEmpty();
			assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("coupon"));
		}

		@Test
		@DisplayName("coupon이 유효한 값이면 validation 통과")
		void validCoupon_passes() {
			CouponUseRequest request = fromMap(Map.of("coupon", "COUPON-CODE"), CouponUseRequest.class);
			Set<ConstraintViolation<CouponUseRequest>> violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}
	}

	@Nested
	@DisplayName("RegisterPublicIdRequest validation")
	class RegisterPublicIdRequestValidation {

		@Test
		@DisplayName("publicId가 null이면 validation 실패")
		void nullPublicId_fails() {
			RegisterPublicIdRequest request = fromMap(Map.of(), RegisterPublicIdRequest.class);
			Set<ConstraintViolation<RegisterPublicIdRequest>> violations = validator.validate(request);
			assertThat(violations).isNotEmpty();
			assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("publicId"));
		}

		@Test
		@DisplayName("publicId가 유효한 값이면 validation 통과")
		void validPublicId_passes() {
			RegisterPublicIdRequest request = fromMap(Map.of("publicId", "myuser"), RegisterPublicIdRequest.class);
			Set<ConstraintViolation<RegisterPublicIdRequest>> violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}
	}

	@Nested
	@DisplayName("ProfileUploadRequest validation")
	class ProfileUploadRequestValidation {

		@Test
		@DisplayName("contentType이 null이면 validation 실패")
		void nullContentType_fails() {
			ProfileUploadRequest request = fromMap(Map.of(), ProfileUploadRequest.class);
			Set<ConstraintViolation<ProfileUploadRequest>> violations = validator.validate(request);
			assertThat(violations).isNotEmpty();
			assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("contentType"));
		}

		@Test
		@DisplayName("contentType이 유효한 값이면 validation 통과")
		void validContentType_passes() {
			ProfileUploadRequest request = fromMap(Map.of("content-type", "image/jpeg"), ProfileUploadRequest.class);
			Set<ConstraintViolation<ProfileUploadRequest>> violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}
	}

	@Nested
	@DisplayName("PostMediaUploadRequest validation")
	class PostMediaUploadRequestValidation {

		@Test
		@DisplayName("contentTypes가 null이면 validation 실패")
		void nullContentTypes_fails() {
			PostMediaUploadRequest request = fromMap(Map.of(), PostMediaUploadRequest.class);
			Set<ConstraintViolation<PostMediaUploadRequest>> violations = validator.validate(request);
			assertThat(violations).isNotEmpty();
		}

		@Test
		@DisplayName("contentTypes가 빈 리스트이면 validation 실패")
		void emptyContentTypes_fails() {
			PostMediaUploadRequest request = fromMap(Map.of("content-types", List.of()), PostMediaUploadRequest.class);
			Set<ConstraintViolation<PostMediaUploadRequest>> violations = validator.validate(request);
			assertThat(violations).isNotEmpty();
		}

		@Test
		@DisplayName("contentTypes에 유효한 값이 있으면 validation 통과")
		void validContentTypes_passes() {
			PostMediaUploadRequest request = fromMap(
					Map.of("content-types", List.of("image/jpeg", "video/mp4")),
					PostMediaUploadRequest.class
			);
			Set<ConstraintViolation<PostMediaUploadRequest>> violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("contentTypes 원소에 빈 문자열이 있으면 validation 실패")
		void blankElement_fails() {
			PostMediaUploadRequest request = fromMap(
					Map.of("content-types", List.of("image/jpeg", "")),
					PostMediaUploadRequest.class
			);
			Set<ConstraintViolation<PostMediaUploadRequest>> violations = validator.validate(request);
			assertThat(violations).isNotEmpty();
		}
	}

	@Nested
	@DisplayName("PostContentRequest validation")
	class PostContentRequestValidation {

		@Test
		@DisplayName("text가 null이면 validation 실패")
		void nullText_fails() {
			PostContentRequest request = fromMap(Map.of(), PostContentRequest.class);
			Set<ConstraintViolation<PostContentRequest>> violations = validator.validate(request);
			assertThat(violations).isNotEmpty();
			assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("text"));
		}

		@Test
		@DisplayName("text가 빈 문자열이면 validation 실패")
		void emptyText_fails() {
			PostContentRequest request = fromMap(Map.of("text", ""), PostContentRequest.class);
			Set<ConstraintViolation<PostContentRequest>> violations = validator.validate(request);
			assertThat(violations).isNotEmpty();
		}

		@Test
		@DisplayName("text만 있으면 validation 통과")
		void validTextOnly_passes() {
			PostContentRequest request = fromMap(Map.of("text", "Hello World"), PostContentRequest.class);
			Set<ConstraintViolation<PostContentRequest>> violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}

		@Test
		@DisplayName("media 항목에 type이 빈 문자열이면 validation 실패")
		void mediaWithBlankType_fails() {
			PostContentRequest request = fromMap(
					Map.of("text", "Post", "media", List.of(Map.of("type", "", "url", "https://example.com/img.jpg"))),
					PostContentRequest.class
			);
			Set<ConstraintViolation<PostContentRequest>> violations = validator.validate(request);
			assertThat(violations).isNotEmpty();
		}

		@Test
		@DisplayName("media 항목에 url이 빈 문자열이면 validation 실패")
		void mediaWithBlankUrl_fails() {
			PostContentRequest request = fromMap(
					Map.of("text", "Post", "media", List.of(Map.of("type", "image", "url", ""))),
					PostContentRequest.class
			);
			Set<ConstraintViolation<PostContentRequest>> violations = validator.validate(request);
			assertThat(violations).isNotEmpty();
		}

		@Test
		@DisplayName("media 항목이 모두 유효하면 validation 통과")
		void validMedia_passes() {
			PostContentRequest request = fromMap(
					Map.of("text", "Post", "media", List.of(Map.of("type", "image", "url", "https://example.com/img.jpg"))),
					PostContentRequest.class
			);
			Set<ConstraintViolation<PostContentRequest>> violations = validator.validate(request);
			assertThat(violations).isEmpty();
		}
	}
}
