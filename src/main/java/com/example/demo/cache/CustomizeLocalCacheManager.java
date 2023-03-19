package com.example.demo.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.stereotype.Component;

/**
 * 本地缓存管理器，使用redis做缓存版本校验，支持分布式缓存更新。
 * 使用本地缓存时需要显示指明 cacheManager = “localCache”
 */
@Component("localCache")
public class CustomizeLocalCacheManager extends CaffeineCacheManager {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private LocalCacheConfig localCacheConfig;

    private volatile com.github.benmanes.caffeine.cache.Cache<Object, Object> cache;

    @Override
    protected Cache createCaffeineCache(String name) {
        return new CustomizeLocalCache(name, getSingletonCache(), redisClient, localCacheConfig);
    }


    /**
     * 防止内存溢出，本地缓存底层使用同一个Cache
     *
     * @return
     */
    private com.github.benmanes.caffeine.cache.Cache<Object, Object> getSingletonCache() {
        if (cache == null) {
            synchronized (this) {
                if (cache == null) {
                    cache = Caffeine.newBuilder().maximumSize(localCacheConfig.getMaximumSize())
                            .expireAfterWrite(localCacheConfig.getDefaultTimeout())
                            .build();
                }
            }
        }
        return cache;
    }

}
