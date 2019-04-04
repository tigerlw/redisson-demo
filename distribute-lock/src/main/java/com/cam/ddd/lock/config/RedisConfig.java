package com.cam.ddd.lock.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisConfig {
	
	@Bean
	public RedissonClient getRedisClient()
	{
		Config config = new Config();

		config.setCodec(new org.redisson.client.codec.StringCodec());
		config.useSingleServer()
		      .setAddress("redis://127.0.0.1:6379");
		
		RedissonClient redisson = Redisson.create(config);
		
		return redisson;
	}

}
