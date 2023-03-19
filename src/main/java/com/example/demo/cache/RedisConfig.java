package com.example.demo.cache;


import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * redis配置，使用Lettuce单节点模式，value的序列化采用阿里fastjson2
 */
@Configuration
@Getter
public class RedisConfig {

    /**
     * 项目统一前缀
     */
    public static final String CACHE_PREFIX = "template:server:";

    @Value("${spring.cache.redis.time-to-live:1d}")
    private Duration defaultExpirationTime;


    @Autowired
    private RedisCacheKeyGenerator redisCacheKeyGenerator;


    @Bean(name = "cacheManager")
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return new CustomRedisCacheManager(RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory), RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(defaultExpirationTime)
                .disableCachingNullValues()
                .computePrefixWith(redisCacheKeyGenerator)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer())));
    }

    @Bean
    public GenericFastJsonRedisSerializer redisSerializer() {
        return new GenericFastJsonRedisSerializer();
    }


    /**
     * redisTemplate配置
     *
     * @param factory
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(factory);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericFastJsonRedisSerializer fastJsonRedisSerializer = redisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(fastJsonRedisSerializer);
        redisTemplate.setHashValueSerializer(fastJsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }


//    @Bean
//    @ConfigurationProperties(prefix = "spring.redis.pool")
//    public JedisPoolConfig jedisPoolConfig() {
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        return jedisPoolConfig;
//    }
//
//    @Bean
//    @ConfigurationProperties(prefix = "spring.redis")
//    public JedisBuilder jedisBuilder() {
//        JedisBuilder jedisBuilder = new JedisBuilder();
//        return jedisBuilder;
//    }
//
//    @Bean
//    public RedisClient redisClient(JedisBuilder jedisBuilder, JedisPoolConfig jedisPoolConfig) {
//        RedisClient redisClient = new RedisClient();
//        redisClient.setJedisPool(jedisBuilder.create(jedisPoolConfig));
//        return redisClient;
//    }
//
//
//
//    @Bean
//    public RedisConnectionFactory redisConnectionFactory(@Autowired JedisPoolConfig jedisPoolConfig, @Autowired JedisBuilder jedisBuilder) {
//        JedisClientConfiguration clientConfig = JedisClientConfiguration.builder()
//                .usePooling().poolConfig(jedisPoolConfig).and().readTimeout(Duration.ofMillis(jedisBuilder.getTimeout())).build();
//
//        // 单点redis
//        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
//        // 哨兵redis
//        // RedisSentinelConfiguration redisConfig = new RedisSentinelConfiguration();
//        // 集群redis
//        // RedisClusterConfiguration redisConfig = new RedisClusterConfiguration();
//        redisConfig.setHostName(jedisBuilder.getHost());
//        redisConfig.setPassword(RedisPassword.of(jedisBuilder.getPassword()));
//        redisConfig.setPort(jedisBuilder.getPort());
//        redisConfig.setDatabase(0);
//        return new JedisConnectionFactory(redisConfig, clientConfig);
//    }


}
