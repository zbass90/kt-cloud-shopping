package com.kt.domain.order;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Receiver {
	@Column(name = "receiver_name")
	private String name;
	@Column(name = "receiver_address")
	private String address;
	@Column(name = "receiver_mobile")
	private String mobile;
}
