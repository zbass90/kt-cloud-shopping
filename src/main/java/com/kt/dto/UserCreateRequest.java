package com.kt.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kt.domain.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserCreateRequest(
	@NotBlank
	Long id,
	@NotBlank
	String loginId,
	@NotBlank
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^])[A-Za-z\\d!@#$%^]{8,}$")
	// 최소8자 이상 대/소문자, 숫자, 특수문자 포함 -> 정규식 사용
	String password,
	@NotBlank
	String name,
	@NotBlank
	@Pattern(regexp = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\\\.[A-Z]{2,}$")
	String email,
	@NotBlank
	@Pattern(regexp = "^(0\\d{1,2})-(\\d{3,4})-(\\d{4})")
	String mobile,
	@NotNull
	Gender gender,
	@NotNull
	LocalDate birthday,
	LocalDateTime createAt,
	LocalDateTime updateAt
) {

}

