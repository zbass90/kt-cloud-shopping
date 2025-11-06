package com.kt.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kt.domain.user.User;
import com.kt.dto.UserCreateRequest;
import com.kt.repository.UserJDBCRepository;
import com.kt.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
	private final UserJDBCRepository userJDBCRepository;
	private final UserRepository userRepository;

	public void create(UserCreateRequest request) {
		userRepository.save(User.of(request));
	}

	@Transactional(readOnly = true)
	public boolean isDuplicateLoginId(String loginId) {
		return userRepository.existsByLoginId(loginId);
	}

	public void changePassword(Long id, String oldPassword, String password) {
		if(!userRepository.existsById(id)) {
			throw new IllegalArgumentException("존재하지 않는 회원입니다.");
		}

		var user = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

		if(!user.getPassword().equals(oldPassword)) {
			throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
		}

		if(oldPassword.equals(password)) {
			throw new IllegalArgumentException("기존 비밀번호와 동일한 비밀번호로 변경할 수 없습니다.");
		}

		user.changePassword(password);
	}

	public Page<User> search(Pageable pageable, String keyword) {
		return userRepository.findAllByNameContaining(keyword, pageable);
	}

	public void update(Long id, String name, String email, String mobile) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

		user.update(name, email, mobile);
	}

	public User detail(Long id) {
		return userJDBCRepository.selectById(id)
			.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
	}
}