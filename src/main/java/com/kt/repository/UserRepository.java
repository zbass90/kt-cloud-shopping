package com.kt.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kt.domain.User;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepository {
	private final JdbcTemplate jdbcTemplate;
	public void save(User user) {
		String sql = """
  			INSERT INTO MEMBER (
  													id,
  													loginId,
  													password,
  													name,
  													birthday,
  													mobile,
  													email,
  													gender,
  													createdAt,
  													updatedAt)
  			VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
		""";
		jdbcTemplate.update(sql,
			user.getId(),
			user.getLoginId(),
			user.getPassword(),
			user.getName(),
			user.getBirthday(),
			user.getMobile(),
			user.getEmail(),
			user.getGender().name(),
			user.getCreatedAt(),
			user.getUpdatedAt()
		);
	}

	public Long selectMaxId() {
		var sql = "SELECT MAX(id) FROM MEMBER";
		var maxId = jdbcTemplate.queryForObject(sql, Long.class);
		return maxId == null ? 0L : maxId;
	}

	// 크게 세가지 정도 아이디 중복 체크 방법
	// 1. count해서 0보다 큰지 체크 -> 전 별로 좋아보이진않음
	// db에 만약 유저가 3000만명 -> 1번 중복체크할때마다 3천개의 데이터를 모두 살펴봐야함(full-scan)
	// 2. unique 제약조건 걸어서 예외 처리 -> 유니크키에러 (DataViolation Exception)별로 좋아보이진 않음
	// 3. exists로 존재 여부 체크 -> boolean으로 값 존재 여부를 바로 알 수 있음
	public boolean existsByLoginId(String loginId) {
		var sql = "SELECT EXISTS (SELECT id FROM MEMBER WHERE loginId = ?)";

		return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, loginId));
	}

	public void updatePassword(int id, String password) {
		// UPDATE {table} SET {column} = {value}, {column} = {value} WHERE {condition}
		var sql = "UPDATE MEMBER SET password = ? WHERE id = ?";

		jdbcTemplate.update(sql, password, id);
	}
}
