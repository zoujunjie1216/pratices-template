package com.example.demo.cache;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * 本地缓存配置类
 */
@Getter
@Configuration
public class LocalCacheConfig {

    /**
     * 本地缓存过期时间，默认1天, 默认过期策略为expireAfterWrite
     */
    @Value("${local.cache.default.timeout:86400}")
    private Duration defaultTimeout;

    @Value("${local.cache.maximumSize:500}")
    private Long maximumSize;

    @Autowired
    private RedisCacheKeyGenerator redisCacheKeyGenerator;

    /**
     * 本地缓存在redis的统一前缀
     */
    public static final String LOCAL_CACHE_PREFIX = "JUN_JIE_LOCAL_CACHE";

}
