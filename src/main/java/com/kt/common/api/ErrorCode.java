package com.kt.common.api;

import org.springframework.http.HttpStatus;

/**
 * 공통 에러 코드 정의
 *
 * httpStatus : 해당 오류의 HTTP 상태 코드
 * messageKey : 다국어 처리용 메시지 키(옵션)
 * defaultMessage : 기본 한글 메시지
 */
public enum ErrorCode {
	COMMON_INVALID_ARGUMENT(HttpStatus.BAD_REQUEST, "error.common.invalid_argument", "잘못된 요청입니다."),
	COMMON_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "error.common.validation_failed", "검증에 실패했습니다."),
	AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "error.auth.unauthorized", "인증이 필요합니다."),
	AUTH_FORBIDDEN(HttpStatus.FORBIDDEN, "error.auth.forbidden", "접근 권한이 없습니다."),
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "error.resource.not_found", "리소스를 찾을 수 없습니다."),
	CONFLICT_STATE(HttpStatus.CONFLICT, "error.common.conflict", "충돌이 발생했습니다."),
	RATE_LIMITED(HttpStatus.TOO_MANY_REQUESTS, "error.common.rate_limited", "요청이 너무 많습니다."),
	INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "error.common.internal", "서버 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String messageKey;
	private final String defaultMessage;

	ErrorCode(HttpStatus httpStatus, String messageKey, String defaultMessage) {
		this.httpStatus = httpStatus;
		this.messageKey = messageKey;
		this.defaultMessage = defaultMessage;
	}

	public HttpStatus getHttpStatus() { return httpStatus; }
	public String getMessageKey() { return messageKey; }
	public String getDefaultMessage() { return defaultMessage; }
}
