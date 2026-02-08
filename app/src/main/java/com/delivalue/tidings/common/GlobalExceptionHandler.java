package com.delivalue.tidings.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
		List<String> errors = e.getBindingResult().getFieldErrors().stream()
				.map(field -> field.getField() + ": " + field.getDefaultMessage())
				.toList();

		return ResponseEntity.badRequest().body(Map.of(
				"status", 400,
				"message", "입력값 검증에 실패했습니다.",
				"errors", errors
		));
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException e) {
		return ResponseEntity.status(e.getStatusCode()).body(Map.of(
				"status", e.getStatusCode().value(),
				"message", e.getReason() != null ? e.getReason() : "Error"
		));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleUnexpected(Exception e) {
		log.error("Unexpected error", e);
		return ResponseEntity.internalServerError().body(Map.of(
				"status", 500,
				"message", "서버 내부 오류가 발생했습니다."
		));
	}
}
