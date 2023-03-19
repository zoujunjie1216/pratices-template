package com.example.demo.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import static com.example.demo.cache.RedisConfig.CACHE_PREFIX;


/**
 * redis缓存统一前缀支持动态版本刷新
 */
@Slf4j
@Component
public class RedisCacheKeyGenerator implements CacheKeyPrefix {

    /**
     * 冒号
     */
    public static final String COLON = ":";

    @Value("${redis.cache.latest.publish.version:1.0.0}")
    public String latestPublishVersion;


    @Override
    public String compute(String cacheName) {
        if (StringUtils.isBlank(cacheName)) {
            throw new IllegalArgumentException("cacheName 不能为空");
        }
        return getCacheKeyPrefix().concat(cacheName).concat(COLON).intern();
    }

    /**
     * 获取通用前缀
     * 通用前缀为:
     * CACHE_PREFIX + 英文冒号 + 最近一次修改的缓存版本号{latestPublishVersion} + 英文冒号
     * 示例: template:server:1.0.0:
     *
     * @return
     */
    public String getCacheKeyPrefix() {
        //通用前缀策略：
        //统一前缀CACHE_PREFIX
        StringBuilder sb = new StringBuilder(CACHE_PREFIX)
                //最近一次修改的缓存版本号{latestPublishVersion}
                .append(latestPublishVersion)
                //英文冒号
                .append(COLON);
        return sb.toString().intern();
    }

    /**
     * 使用指定的前缀和cacheName计算前缀
     * 示例： template:server:1.0.0:test:
     *
     * @param cachePrefix 自定义前缀
     * @param cacheName   缓存空间
     * @return
     */
    public String computePrefix(@Nullable String cachePrefix, String cacheName) {
        if (StringUtils.isBlank(cachePrefix)) {
            return compute(cacheName);
        }
        StringBuilder sb = new StringBuilder(getCacheKeyPrefix());
        sb.append(cachePrefix);
        if (!cachePrefix.endsWith(COLON)) {
            sb.append(COLON);
        }
        sb.append(cacheName);
        sb.append(COLON);
        return sb.toString();
    }

    /**
     * 给指定的key添加统一前缀，如果key已经有前缀了，则直接返回，适用于手动操作redis使用
     * 示例： template:server:1.0.0:test
     *
     * @param key redis的key,不能为null
     * @return
     */
    public String prefixKey(String key) {
        if (StringUtils.isBlank(key)) {
            throw new IllegalArgumentException("key 不能为空");
        }
        String prefix = getCacheKeyPrefix();
        if (key.startsWith(prefix)) {
            return key;
        }
        return getCacheKeyPrefix().concat(key);
    }

}
