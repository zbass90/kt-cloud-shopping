package com.kt.domain;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.kt.dto.UserCreateRequest;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String loginId;
	private String password;
	private String name;
	private String email;
	private String mobile;
	private Gender gender;
	private LocalDate birthday;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	@Builder(access = AccessLevel.PRIVATE)
	public User(String loginId, String password, String name, String email, String mobile, Gender gender,
		LocalDate birthday, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.loginId = loginId;
		this.password = password;
		this.name = name;
		this.email = email;
		this.mobile = mobile;
		this.gender = gender;
		this.birthday = birthday;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void update(String name, String email, String mobile) {
		this.name = name;
		this.email = email;
		this.mobile = mobile;
		this.updatedAt = LocalDateTime.now();
	}

	public static User of(UserCreateRequest request) {
		return User.builder()
			.loginId(request.loginId())
			.password(request.password())
			.name(request.name())
			.email(request.email())
			.mobile(request.mobile())
			.gender(request.gender())
			.birthday(request.birthday())
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();
	}

	public static User fromJdbc(ResultSet rs) throws SQLException {
		String genderStr = rs.getString("gender");
		User u = User.builder()
			.loginId(rs.getString("login_id"))
			.password(rs.getString("password"))
			.name(rs.getString("name"))
			.email(rs.getString("email"))
			.mobile(rs.getString("mobile"))
			.gender(genderStr != null ? Gender.valueOf(genderStr) : null)
			.birthday(rs.getObject("birthday", LocalDate.class))
			.createdAt(rs.getObject("created_at", LocalDateTime.class))
			.updatedAt(rs.getObject("updated_at", LocalDateTime.class))
			.build();
		u.id = rs.getLong("id");
		return u;

	}

}

