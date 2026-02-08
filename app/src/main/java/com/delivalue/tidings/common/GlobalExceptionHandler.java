package com.delivalue.tidings.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

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
