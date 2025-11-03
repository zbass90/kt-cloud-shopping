package com.kt.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.kt.dto.UserCreateRequest;
import com.kt.dto.UserUpdatePasswordRequest;
import com.kt.service.UserService;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;

	@ApiResponses(value = {
		@ApiResponse(responseCode = "400", description = "유효성 검사 필패"),
		@ApiResponse(responseCode = "500", description = "서버 에러 - 백엔드에 바로 문의 바랍니다.")
	})
	@Tag(name = "유저", description = "유저 관련 API")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void create(@Valid @RequestBody UserCreateRequest request) {
		userService.create(request);
	}


	// /users/duplicate-login-id?loginId=ktuser
	// IllegalArgumentException 발생 시 400에러
	// GET에서 쓰는 queryString
	// @RequestParam의 속성은 기본이 required = true
	@GetMapping("/duplicate-login-id")
	@ResponseStatus(HttpStatus.OK)
	public Boolean isDuplicateLoginId(@RequestParam String loginId) {
		return userService.isDuplicateLoginId(loginId);
	}

	//uri는 식별이 가능해야한다.
	// 유저들x , 어떤 유저?
	// /users/update-password
	// body => json으로 넣어서 보내고

	// 1. 바디에 id값을 같이 받는다
	// 2. uri에 id값을 넣는다. /users/{id}/update-password
	// 3. 인증/인가 객체에서 id값을 꺼낸다. (V)
	@PutMapping("/{id}/update-password")
	@ResponseStatus(HttpStatus.OK)
	public void updatePassword(
		@PathVariable Integer id,
		@RequestBody @Valid UserUpdatePasswordRequest request
	) {
		userService.changePassword(id, request.oldPassword(), request.newPassword());
	}
}

