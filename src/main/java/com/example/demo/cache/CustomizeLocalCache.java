package com.example.demo.cache;

import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

import static com.example.demo.cache.LocalCacheConfig.LOCAL_CACHE_PREFIX;


/**
 * 基于redis键值对做缓存版本校验，和清除功能的本地缓存实现
 * 内部缓存实际使用的是Caffeine
 */
@Slf4j
public class CustomizeLocalCache extends CaffeineCache {

    /**
     * 本地缓存版本记录，存储key
     */
    private volatile Cache<String, VersionObject> versionMap;

    /**
     * 记录总缓存空间数
     */
    private static final AtomicInteger atomicInteger = new AtomicInteger(0);

    /**
     * redisClient
     */
    private RedisClient redisClient;


    /**
     * 本地缓存配置
     */
    private LocalCacheConfig localCacheConfig;

    /**
     * 解析后的缓存空间名
     */
    private String finalName;

    /**
     * 过期时间设置，配置中的过期时间不为0时，使用配置过期时间
     */
    private Duration expireTime = CacheSupport.DEFAULT_TTL;

    public CustomizeLocalCache(String name, Cache<Object, Object> cache, RedisClient redisClient, LocalCacheConfig localCacheConfig) {
        super(name, cache);
        Assert.notNull(redisClient, "redisClient must not be null");
        Assert.notNull(localCacheConfig, "localCacheConfig must not be null");
        this.redisClient = redisClient;
        this.localCacheConfig = localCacheConfig;
        initialize();
        log.info("创建本地缓存, 缓存name:{}, cacheHash:{}, 总缓存空间count:{}", name, cache.toString(), atomicInteger.incrementAndGet());
    }

    protected void initialize() {
        Pair<String, Duration> pair = CacheSupport.getNameAndDefaultTimeOut(super.getName());
        this.finalName = pair.getKey();
        if (Objects.isNull(pair.getValue())) {
            this.expireTime = localCacheConfig.getDefaultTimeout();
        } else {
            this.expireTime = pair.getValue();
        }
        versionMap = Caffeine.newBuilder().maximumSize(localCacheConfig.getMaximumSize()).
                expireAfterWrite(expireTime.plusSeconds(CacheSupport.CACHE_RANDOM_RANGE)).build();
    }

    @Override
    protected Object lookup(Object key) {
        String realKey = getRealKey(key.toString());
        checkVersion(realKey);
        return super.lookup(realKey);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        String realKey = getRealKey(key.toString());
        checkVersion(realKey);
        return super.get(realKey, valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        String realKey = getRealKey(key.toString());
        versionMap.get(realKey, k -> generateNewVersion(realKey));
        super.put(realKey, value);
    }


    @Override
    public void evict(Object key) {
        String realKey = getRealKey(key.toString());
        redisClient.del(realKey);
    }


    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        String realKey = getRealKey(key.toString());
        return super.putIfAbsent(realKey, value);
    }

    @Override
    public boolean evictIfPresent(Object key) {
        String realKey = getRealKey(key.toString());
        return super.evictIfPresent(realKey);
    }


    private void checkVersion(String realKey) {
        int retryTimes = 10;
        int sleepMillis = 100;
        boolean result = checkOrResetVersion(realKey);
        // 如果获取锁失败，按照传入的重试次数进行重试
        while ((!result) && retryTimes-- > 0) {
            try {
                log.debug("checkVersion failed, retrying..." + retryTimes);
                Thread.sleep(sleepMillis);
            } catch (InterruptedException e) {
                log.warn("Interrupted!", e);
                Thread.currentThread().interrupt();
            }
            result = checkOrResetVersion(realKey);
        }
    }

    /**
     * 本地缓存和远程唯一缓存版本校验，校验失败会清除本地缓存
     *
     * @param realKey
     */
    private boolean checkOrResetVersion(String realKey) {
        VersionObject localVersion = versionMap.get(realKey, k -> generateNewVersion(realKey));
        String value = redisClient.get(realKey);
        VersionObject remoteVersion = getVersionObject(value);
        if (StrUtil.isEmpty(remoteVersion.getVersion())) {
            //无唯一缓存版本，则抢占式设置redis缓存唯一版本
            VersionObject newVersion = generateNewVersion(realKey);
            long expireTimeOfSeconds = CacheSupport.plusRandomSeconds(expireTime).getSeconds();
            boolean success = redisClient.tryGetDistributedLock(realKey, newVersion.getVersion(), expireTimeOfSeconds);
            if (success) {
                //设置本地唯一缓存版本，删除本地缓存
                versionMap.put(realKey, newVersion);
                super.evict(realKey);
                return true;
            } else {
                return false;
            }
        } else {
            //和远程版本一致，则不需要处理
            if (remoteVersion.equals(localVersion)) {
                return true;
            } else {
                //不一致则说明本地缓存过期，同步更新本地缓存到远程版本,删除本地缓存
                synchronized (localVersion) {
                    VersionObject currentVersion = versionMap.getIfPresent(realKey);
                    if (remoteVersion.equals(currentVersion)) {
                        return true;
                    }
                    versionMap.put(realKey, remoteVersion);
                    super.evict(realKey);
                    return true;
                }
            }
        }
    }

    /**
     * String封装VersionObject
     *
     * @param version
     * @return
     */
    private VersionObject getVersionObject(String version) {
        return new VersionObject(version);
    }

    private VersionObject generateNewVersion(String key) {
        String newVersion = key + "-" + UUID.randomUUID().toString().replace("-", "");
        return new VersionObject(newVersion);
    }


    /**
     * Key值封装,统一前缀+命名空间+key值
     *
     * @param key
     * @return
     */
    private String getRealKey(String key) {
        return localCacheConfig.getRedisCacheKeyGenerator().computePrefix(LOCAL_CACHE_PREFIX, finalName).concat(key);
    }
}
