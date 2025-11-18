package com.kt.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

	private final RedisProperties redisProperties;
	private static final String REDISSON_ADDRESS_FORMAT = "redis://%s:%s";

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		String uri = String.format(REDISSON_ADDRESS_FORMAT, redisProperties.getHost(), redisProperties.getPort());
		config.useSingleServer().setAddress(uri);
		return Redisson.create(config);
	}
}
