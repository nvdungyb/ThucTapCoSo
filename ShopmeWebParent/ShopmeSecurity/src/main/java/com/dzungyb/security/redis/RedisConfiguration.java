package com.security.security.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class RedisConfiguration {
    @Value("${redis_host_name}")
    private String redisHostName;

    @Value("${redis_port}")
    private int redisPort;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setHostName(redisHostName);
        jedisConnectionFactory.setPort(redisPort);
        return jedisConnectionFactory;
    }

    /**
     * RedisTemplate trong Spring với cấu hình mặc định JdkSerializationRedisSerializer , có thể đã serialize đối tượng java thành dạng byte[].
     * Để lưu trữ dữ liệu dạng String, cần cấu hình lại RedisTemplate với StringRedisSerializer.
     *
     * @param jedisConnectionFactory
     * @return RedisTemplate<String, String>
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.setConnectionFactory(jedisConnectionFactory);
        return template;
    }
}
