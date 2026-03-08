package com.delivalue.tidings.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

	private GlobalExceptionHandler handler;

	@BeforeEach
	void setUp() {
		handler = new GlobalExceptionHandler();
	}

	@Nested
	@DisplayName("handleResponseStatus - ResponseStatusException 처리")
	class HandleResponseStatus {

		@Test
		@DisplayName("404 NOT_FOUND 예외를 올바른 상태 코드와 메시지로 변환한다")
		void notFound_returnsCorrectResponse() {
			ResponseStatusException exception = new ResponseStatusException(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다");

			ResponseEntity<Map<String, Object>> response = handler.handleResponseStatus(exception);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().get("status")).isEqualTo(404);
			assertThat(response.getBody().get("message")).isEqualTo("리소스를 찾을 수 없습니다");
		}

		@Test
		@DisplayName("400 BAD_REQUEST 예외를 올바르게 처리한다")
		void badRequest_returnsCorrectResponse() {
			ResponseStatusException exception = new ResponseStatusException(HttpStatus.BAD_REQUEST, "잘못된 요청입니다");

			ResponseEntity<Map<String, Object>> response = handler.handleResponseStatus(exception);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().get("status")).isEqualTo(400);
			assertThat(response.getBody().get("message")).isEqualTo("잘못된 요청입니다");
		}

		@Test
		@DisplayName("401 UNAUTHORIZED 예외를 올바르게 처리한다")
		void unauthorized_returnsCorrectResponse() {
			ResponseStatusException exception = new ResponseStatusException(HttpStatus.UNAUTHORIZED);

			ResponseEntity<Map<String, Object>> response = handler.handleResponseStatus(exception);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().get("status")).isEqualTo(401);
			assertThat(response.getBody().get("message")).isEqualTo("Error");
		}

		@Test
		@DisplayName("reason이 null인 경우 기본 메시지 'Error'를 반환한다")
		void nullReason_returnsDefaultMessage() {
			ResponseStatusException exception = new ResponseStatusException(HttpStatus.FORBIDDEN);

			ResponseEntity<Map<String, Object>> response = handler.handleResponseStatus(exception);

			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().get("message")).isEqualTo("Error");
		}

		@Test
		@DisplayName("500 INTERNAL_SERVER_ERROR 예외를 올바르게 처리한다")
		void internalServerError_returnsCorrectResponse() {
			ResponseStatusException exception = new ResponseStatusException(
					HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류"
			);

			ResponseEntity<Map<String, Object>> response = handler.handleResponseStatus(exception);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().get("status")).isEqualTo(500);
		}
	}

	@Nested
	@DisplayName("handleValidation - MethodArgumentNotValidException 처리")
	class HandleValidation {

		private MethodArgumentNotValidException createValidationException(FieldError... fieldErrors) {
			BindingResult bindingResult = new MapBindingResult(new HashMap<>(), "request");
			for (FieldError error : fieldErrors) {
				bindingResult.addError(error);
			}
			try {
				MethodParameter parameter = MethodParameter.forExecutable(
						GlobalExceptionHandlerTest.class.getDeclaredMethod("setUp"), -1
				);
				return new MethodArgumentNotValidException(parameter, bindingResult);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}

		@Test
		@DisplayName("validation 실패 시 400 상태 코드와 필드 오류 목록을 반환한다")
		void validationFailure_returns400WithErrors() {
			MethodArgumentNotValidException exception = createValidationException(
					new FieldError("request", "follow", "must not be blank")
			);

			ResponseEntity<Map<String, Object>> response = handler.handleValidation(exception);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().get("status")).isEqualTo(400);
			assertThat(response.getBody().get("message")).isEqualTo("입력값 검증에 실패했습니다.");

			@SuppressWarnings("unchecked")
			List<String> errors = (List<String>) response.getBody().get("errors");
			assertThat(errors).isNotEmpty();
			assertThat(errors.get(0)).contains("follow");
		}

		@Test
		@DisplayName("여러 필드 오류가 있으면 모두 errors 리스트에 포함된다")
		void multipleFieldErrors_allIncludedInResponse() {
			MethodArgumentNotValidException exception = createValidationException(
					new FieldError("request", "text", "must not be blank"),
					new FieldError("request", "type", "must not be blank")
			);

			ResponseEntity<Map<String, Object>> response = handler.handleValidation(exception);

			@SuppressWarnings("unchecked")
			List<String> errors = (List<String>) response.getBody().get("errors");
			assertThat(errors).hasSize(2);
			assertThat(errors).anyMatch(err -> err.contains("text"));
			assertThat(errors).anyMatch(err -> err.contains("type"));
		}
	}

	@Nested
	@DisplayName("handleUnexpected - 예상치 못한 예외 처리")
	class HandleUnexpected {

		@Test
		@DisplayName("일반 Exception을 500 상태 코드로 처리한다")
		void generalException_returns500() {
			Exception exception = new RuntimeException("unexpected error");

			ResponseEntity<Map<String, Object>> response = handler.handleUnexpected(exception);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().get("status")).isEqualTo(500);
			assertThat(response.getBody().get("message")).isEqualTo("서버 내부 오류가 발생했습니다.");
		}

		@Test
		@DisplayName("NullPointerException을 500 상태 코드로 처리한다")
		void nullPointerException_returns500() {
			Exception exception = new NullPointerException("null reference");

			ResponseEntity<Map<String, Object>> response = handler.handleUnexpected(exception);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
			assertThat(response.getBody()).isNotNull();
			assertThat(response.getBody().get("status")).isEqualTo(500);
		}

		@Test
		@DisplayName("IllegalArgumentException을 500 상태 코드로 처리한다")
		void illegalArgumentException_returns500() {
			Exception exception = new IllegalArgumentException("bad argument");

			ResponseEntity<Map<String, Object>> response = handler.handleUnexpected(exception);

			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
