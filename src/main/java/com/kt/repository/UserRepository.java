package com.kt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kt.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Boolean existsByLoginId(String loginId);
	Page<User> findAllByNameContaining(String name, Pageable pageable);

}
