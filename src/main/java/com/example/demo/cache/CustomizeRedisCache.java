package com.example.demo.cache;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.time.Duration;
import java.util.Objects;

/**
 * 自定义redisCache 增强@Cacheable支持自定义过期时间
 */
public class CustomizeRedisCache extends RedisCache {

    private final RedisCacheWriter redisCacheWriter;
    private final RedisCacheConfiguration configuration;


    /**
     * 现有 key 值格式为 key#ttl; 该方法将key值后边的 #ttl 去掉; 例如 test# 10; 该方法处理后为test
     */
    private String finalName;

    /**
     * 过期时间设置，配置中的过期时间不为0时，使用配置过期时间
     */
    private Duration expireTime = CacheSupport.DEFAULT_TTL;

    /**
     * Create new {@link CustomizeRedisCache}.
     *
     * @param name        must not be {@literal null}.
     * @param cacheWriter must not be {@literal null}.
     * @param cacheConfig must not be {@literal null}.
     */
    protected CustomizeRedisCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
        redisCacheWriter = cacheWriter;
        configuration = cacheConfig;
        initialize();
    }

    protected void initialize() {
        //过期时间不为0时设置过期时间
        if (!Objects.equals(Duration.ZERO, configuration.getTtl())) {
            expireTime = configuration.getTtl();
        }
        Pair<String, Duration> pair = CacheSupport.getNameAndDefaultTimeOut(super.getName());
        this.finalName = pair.getKey();
        if (Objects.nonNull(pair.getValue())) {
            this.expireTime = pair.getValue();
        }
    }


    /**
     * 重写cache put 逻辑，引入自定义TTL 实现
     * 实现逻辑:
     * 1.通过获取@Cacheable 中的value ,然后根据约定好的特殊字符进行分割
     * 2.从分割结果集中获取设置的TTL 时间并将value 中的，然后给当前缓存设置TTL
     *
     * @param key
     * @param value
     */
    @Override
    public void put(Object key, Object value) {
        String name = super.getName();
        //获取缓存value
        Object cacheValue = preProcessCacheValue(value);
        //获取value 为null 时，抛出异常
        if (!isAllowNullValues() && cacheValue == null) {
            throw new IllegalArgumentException(String.format(
                    "Cache '%s' does not allow 'null' values. Avoid storing null via '@Cacheable(unless=\"#result == null\")' or configure RedisCache to allow 'null' via RedisCacheConfiguration.",
                    name));
        }
        Duration actualExpireTime = CacheSupport.plusRandomSeconds(expireTime);
        //插入时添加时间
        redisCacheWriter.put(finalName, serializeCacheKey(createCacheKey(key)), serializeCacheValue(cacheValue), actualExpireTime);
    }


    /**
     * 现有 key 值格式为 key#ttl; 该方法将key值后边的 #ttl 去掉; 例如 test# 10; 该方法处理后为test
     *
     * @param key will never be {@literal null}.
     * @return
     */
    @Override
    protected String createCacheKey(Object key) {
        String convertedKey = convertKey(key);
        if (!configuration.usePrefix()) {
            return convertedKey;
        }
        return configuration.getKeyPrefixFor(finalName) + key;
    }

}
