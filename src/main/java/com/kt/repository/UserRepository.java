package com.kt.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.util.Pair;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.kt.domain.Gender;
import com.kt.domain.User;
import com.kt.dto.CustomPage;

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

	public void updatePassword(long id, String password) {
		// UPDATE {table} SET {column} = {value}, {column} = {value} WHERE {condition}
		var sql = "UPDATE MEMBER SET password = ? WHERE id = ?";

		jdbcTemplate.update(sql, password, id);
	}

	public boolean existsById(long id) {
		var sql = "SELECT EXISTS (SELECT id FROM MEMBER WHERE id = ?)";

		return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id));
	}

	// id값으로 db에 조회해서 User 객체를 반환하는 메서드가 필요
	public Optional<User> selectById(long id) {
		var sql = "SELECT * FROM MEMBER WHERE id = ?";
		// ResultSet객체로 반환을 함

		var list = jdbcTemplate.query(sql, rowMapper(), id);

		return list.stream().findFirst();
	}

	public Pair<List<User>, Long> selectAll(int page, int size, String keyword) {
		// paging의 구조
		// 백엔드 입장에서 필요한 것
		// 한화면에 몇개 보여줄것인가? => limit
		// 내가 몇번째 페이지를 보고있나? => offset (몇개를 건너뛸것인가?)
		// 보고있는 페이지 - 1 * limit
		// 키워드 검색 = LIKE %keyword% (포함) , %keyword(시작하는), keyword%(끝나는)
		var sql = "SELECT * FROM MEMBER WHERE name LIKE CONCAT('%', ? ,'%') LIMIT ? OFFSET ?";

		var users = jdbcTemplate.query(sql, rowMapper(), keyword, size, page);

		var countSql = "SELECT COUNT(*) FROM MEMBER WHERE name LIKE CONCAT('%', ? ,'%')";
		var totalElements = jdbcTemplate.queryForObject(countSql, Long.class, keyword);

		return Pair.of(users, totalElements);
	}

	public void updateById(Long id, String name, String email, String mobile) {
		var sql = "UPDATE MEMBER SET name = ?, email = ?, mobile = ? WHERE id = ?";

		jdbcTemplate.update(sql, name, email, mobile, id);
	}

	private RowMapper<User> rowMapper() {
		return (rs, rowNum) -> mapToUser(rs);
		// () -> {} 람다는 단일 실행문이면 {} 와 return 생략이 가능하다
	}

	private User mapToUser(ResultSet rs) throws SQLException {
		return new User(
			rs.getLong("id"),
			rs.getString("loginId"),
			rs.getString("password"),
			rs.getString("name"),
			rs.getString("email"),
			rs.getString("mobile"),
			Gender.valueOf(rs.getString("gender")),
			rs.getObject("birthday", LocalDate.class),
			rs.getObject("createdAt", LocalDateTime.class),
			rs.getObject("updatedAt", LocalDateTime.class)
		);
	}
}
