package com.seabuhi.seacredit;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestRedisConfig {

    @Bean
    @Primary
    public StringRedisTemplate testRedisTemplate() {
        StringRedisTemplate mockTemplate = Mockito.mock(StringRedisTemplate.class);
        ValueOperations<String, String> mockValueOps = Mockito.mock(ValueOperations.class);
        
        when(mockTemplate.opsForValue()).thenReturn(mockValueOps);
        
        // Mock setIfAbsent for Idempotency
        when(mockValueOps.setIfAbsent(anyString(), anyString(), anyLong(), any())).thenReturn(true)
                .thenReturn(false); // Second call returns false for idempotency test
        
        // Mock increment for Rate Limiting
        when(mockValueOps.increment(anyString())).thenReturn(1L);
        
        return mockTemplate;
    }

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return Mockito.mock(RedisConnectionFactory.class);
    }
}
