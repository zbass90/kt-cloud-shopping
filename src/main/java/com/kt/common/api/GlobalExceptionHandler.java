package com.kt.common.api;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;


/**
 * 전역 예외 처리
 *
 * Spring MVC 전역 범위에서 발생하는 예외를 일괄 처리하여
 * RFC 7807 형식의 {@link org.springframework.http.ProblemDetail} 응답을 반환
 *
 * 처리 대상:
 * - BusinessException : 비즈니스 로직 예외
 * - MethodArgumentNotValidException : 요청 본문 @Valid 검증 실패
 * - ConstraintViolationException : 파라미터 @Valid 검증 실패
 * - HttpMessageNotReadableException : JSON 파싱 불가 등 요청 본문 오류
 * - AccessDeniedException : 접근 권한 부족
 * - Exception : 기타 예기치 못한 오류
 *
 * 각 예외는 {@link ErrorCode}를 기반으로 HTTP 상태, 메시지 키, 기본 메시지를 설정이 가능함
 * 추가로 요청 ID(X-Request-Id) 및 오류 상세 정보(errors)를 포함할 수 있음
 *
 * @see org.springframework.http.ProblemDetail
 * @see org.springframework.web.bind.annotation.RestControllerAdvice
 * @see ErrorCode
 */

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ProblemDetail handleBusiness(BusinessException ex, HttpServletRequest req) {
		log.warn("[BUSINESS] code={} msg={} reqId={}", ex.getErrorCode(), ex.getMessage(), req.getHeader("X-Request-Id"));
		return problem(ex.getErrorCode(), ex.getMessage(), req, "business");
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ProblemDetail handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
		log.warn("[VALIDATION] msg={} reqId={}", ex.getMessage(), req.getHeader("X-Request-Id"));
		ErrorCode code = ErrorCode.COMMON_VALIDATION_FAILED;
		ProblemDetail pd = problem(code, code.getDefaultMessage(), req, "validation");

		Map<String, String> fieldErrors = new HashMap<>();
		for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
			fieldErrors.put(fe.getField(), fe.getDefaultMessage());
		}
		pd.setProperty("errors", fieldErrors);
		return pd;
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ProblemDetail handleConstraint(ConstraintViolationException ex, HttpServletRequest req) {
		log.warn("[CONSTRAINT] msg={} reqId={}", ex.getMessage(), req.getHeader("X-Request-Id"));
		ErrorCode code = ErrorCode.COMMON_VALIDATION_FAILED;
		ProblemDetail pd = problem(code, code.getDefaultMessage(), req, "validation");

		Map<String, String> violations = new HashMap<>();
		for (ConstraintViolation<?> v : ex.getConstraintViolations()) {
			violations.put(v.getPropertyPath().toString(), v.getMessage());
		}
		pd.setProperty("errors", violations);
		return pd;
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ProblemDetail handleBadBody(HttpMessageNotReadableException ex, HttpServletRequest req) {
		log.warn("[BAD_BODY] msg={} reqId={}", ex.getMessage(), req.getHeader("X-Request-Id"));
		return problem(ErrorCode.COMMON_INVALID_ARGUMENT, "요청 본문을 읽을 수 없습니다.", req, "bad_request");
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ProblemDetail handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
		log.warn("[FORBIDDEN] msg={} reqId={}", ex.getMessage(), req.getHeader("X-Request-Id"));
		return problem(ErrorCode.AUTH_FORBIDDEN, ex.getMessage(), req, "security");
	}

	@ExceptionHandler(Exception.class)
	public ProblemDetail handleUnexpected(Exception ex, HttpServletRequest req) {
		log.error("[UNEXPECTED] msg={} reqId={}", ex.getMessage(), req.getHeader("X-Request-Id"), ex);
		return problem(ErrorCode.INTERNAL_ERROR, ex.getMessage(), req, "unexpected");
	}

	private ProblemDetail problem(ErrorCode code, String detail, HttpServletRequest req, String category) {
		ProblemDetail pd = ProblemDetail.forStatusAndDetail(code.getHttpStatus(), detail);
		pd.setTitle(code.name());
		pd.setType(URI.create("https://errors.example.com/" + category + "/" + code.name().toLowerCase()));
		pd.setProperty("code", code.name());
		pd.setProperty("messageKey", code.getMessageKey());

		String requestId = req.getHeader("X-Request-Id");
		if (requestId != null && !requestId.isBlank()) {
			pd.setProperty("requestId", requestId);
		}
		return pd;
	}
}