package com.kt.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.kt.domain.User;
import com.kt.dto.UserCreateRequest;
import com.kt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	public void create(UserCreateRequest request) {
		var newUser = new User(
			userRepository.selectMaxId() + 1,
			request.loginId(),
			request.password(),
			request.name(),
			request.email(),
			request.mobile(),
			request.gender(),
			request.birthday(),
			LocalDateTime.now(),
			LocalDateTime.now()
		);

		userRepository.save(newUser);

		// TODO : 아이디 중복 검사
	}
}