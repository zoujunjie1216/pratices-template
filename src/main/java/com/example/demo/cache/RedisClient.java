package com.example.demo.cache;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by god on 15/5/31.
 */
@Slf4j
@Component
public class RedisClient {

    @Autowired
    private RedisTemplate redisTemplate;

    private static final Long UNLOCK_SUCCESS_RESULT = 1L;


    /**
     * ------------------String相关操作--------------------------------
     */

    public void set(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception ex) {
            log.error("redisClient set err, key:{}, value:{}, ex:{}", key, value, ex);
        }
    }

    public void set(String key, String value, long expire) {
        try {
            redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("redisClient set err, key:{}, value:{}, expire:{}, ex:{}", key, value, expire, ex);
        }
    }

    public void set(String key, String value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
        } catch (Exception ex) {
            log.error("redisClient set err, key:{}, value:{}, time:{}, ex:{}", key, value, time, ex);
        }
    }

    public boolean setnx(String key, String value) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value);
        } catch (Exception ex) {
            log.error("redisClient.setnx err, key:{}, value:{}, ex:{}", key, value, ex);
        }
        return false;
    }

    public String get(String key) {
        try {
            return (String) redisTemplate.opsForValue().get(key);
        } catch (Exception ex) {
            log.error("redisClient.get err, key:{}, ex:{}", key, ex);
        }
        return null;
    }

    public Set<String> keys(String pattern) {
        try {
            return redisTemplate.keys(pattern);
        } catch (Exception ex) {
            log.error("redisClient.keys err, pattern:{}, ex:{}", pattern, ex);
        }
        return null;
    }

    public boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception ex) {
            log.error("redisClient.exists err, key:{}, ex:{}", key, ex);
        }
        return false;
    }


    public Long ttl(String key) {
        try {
            return redisTemplate.getExpire(key);
        } catch (Exception ex) {
            log.error("redisClient.ttl err, key:{}, ex:{}", key, ex);

        }
        return 0L;
    }

    public void expire(String key, long seconds) {
        try {
            if (seconds > 0) {
                redisTemplate.expire(key, seconds, TimeUnit.SECONDS);
            }
        } catch (Exception ex) {
            log.error("redisClient.expire err, key:{}, seconds:{}, ex:{}", key, seconds, ex);
        }
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间
     * @param time timeUnit
     * @return
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                return redisTemplate.expire(key, time, timeUnit);
            }
        } catch (Exception ex) {
            log.error("redisClient.expire err, key:{}, time:{}, timeUnit:{}, ex:{}", key, time, timeUnit, ex);
        }
        return false;
    }

    public Long incr(String key) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception ex) {
            log.error("redisClient.incr err, key:{}, ex:{}", key, ex);
        }
        return null;

    }

    public Long incrBy(String key, Long increment) {
        try {
            return redisTemplate.opsForValue().increment(key, increment);
        } catch (Exception ex) {
            log.error("redisClient.incrBy err, key:{}, increment:{}, ex:{}", key, increment, ex);
        }
        return null;

    }

    public Long decr(String key) {
        try {
            return redisTemplate.opsForValue().decrement(key);
        } catch (Exception ex) {
            log.error("redisClient.decr err, key:{}, ex:{}", key, ex);
        }
        return null;

    }

    public Long decrBy(String key, Long increment) {
        try {
            return redisTemplate.opsForValue().decrement(key, increment);
        } catch (Exception ex) {
            log.error("redisClient.decrBy err, key:{}, increment:{}, ex:{}", key, increment, ex);
        }
        return null;

    }

    public void del(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception ex) {
            log.error("redisClient.del err, key:{}, ex:{}", key, ex);
        }
    }


    public void putObject(String key, Object value) {
        Objects.requireNonNull(value);
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception ex) {
            log.error("redisClient.putObject err, key:{}, value:{}, ex:{}", key, JSON.toJSONString(value), ex);
        }
    }

    public void putObject(String key, Object value, long expire) {
        Objects.requireNonNull(value);
        try {
            redisTemplate.opsForValue().set(key, value, expire, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("redisClient.putObject err, key:{}, value:{}, expire:{}, ex:{}", key, JSON.toJSONString(value), expire, ex);
        }
    }


    public <T extends Serializable> T getObject(String key) {
        try {
            return (T) redisTemplate.opsForValue().get(key);
        } catch (Exception ex) {
            log.error("redisClient.getObject err, key:{}, ex:{}", key, ex);
        }
        return null;
    }

    /**
     * ------------------hash相关操作--------------------------------
     */


    public Object hget(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field);
        } catch (Exception ex) {
            log.error("redisClient.hget err, key:{}, field:{}, ex:{}", key, field, ex);
        }
        return null;
    }

    public void hset(String key, String field, String value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception ex) {
            log.error("redisClient.hset err, key:{}, val:{}", key, value, ex);
        }
    }

    public Long hsetnx(String key, String field, String value) {
        try {
            redisTemplate.opsForHash().putIfAbsent(key, field, value);
        } catch (Exception ex) {
            log.error("redisClient.hsetnx err, key:{}, field:{}, value:{}, ex:{}", key, field, value, ex);
        }
        return null;
    }


    public List<String> mget(String... keys) {
        try {
            return redisTemplate.opsForValue().multiGet(ListUtil.toList(keys));
        } catch (Exception ex) {
            log.error("redisClient.mget err, keys:{}, ex:{}", JSON.toJSONString(keys), ex);
        }
        return null;
    }

    public void mset(Map<String, Object> map) {
        try {
            redisTemplate.opsForValue().multiSet(map);
        } catch (Exception ex) {
            log.error("redisClient.mset err, keys:{}, ex:{}", JSON.toJSONString(map), ex);
        }
    }

    public void hmset(String key, Map<String, String> hash) {
        try {
            redisTemplate.opsForHash().putAll(key, hash);
        } catch (Exception ex) {
            log.error("redisClient.hmset err, key:{}, hash:{}, ex:{}", key, JSON.toJSONString(hash), ex);
        }
    }


    public List<String> hmget(String key, List<String> fields) {
        try {
            return redisTemplate.opsForHash().multiGet(key, fields);
        } catch (Exception ex) {
            log.error("redisClient.hmget err, key:{}, val:{}, ex:{}", key, JSON.toJSONString(fields), ex);
        }
        return null;
    }


    public Map<String, String> hgetAll(String key) {
        try {
            return redisTemplate.opsForHash().entries(key);
        } catch (Exception ex) {
            log.error("redisClient.hgetAll err, key:{},ex:{} ", key, ex);
        }
        return null;
    }


    public Long hlen(String key) {
        try {
            return redisTemplate.opsForHash().size(key);
        } catch (Exception ex) {
            log.error("redisClient.hlen err, key:{}", key, ex);
        }
        return null;
    }

    public void hdel(String key, String... field) {
        try {
            redisTemplate.opsForHash().delete(key, field);
        } catch (Exception ex) {
            log.error("redisClient.hdel err, key:{}, field:{}, ex:{}", key, JSON.toJSONString(field), ex);
        }
    }


    public Long hincrBy(String key, String field, Long increment) {
        try {
            return redisTemplate.opsForHash().increment(key, field, increment);
        } catch (Exception ex) {
            log.error("redisClient.hincrBy err, key:{}, field:{}, increment:{}, ex:{}", key, field, increment, ex);
        }
        return null;
    }

    public boolean hexists(String key, String field) {
        try {
            return redisTemplate.opsForHash().hasKey(key, field);
        } catch (Exception ex) {
            log.error("redisClient.hexists err, key:{}, field:{}, ex:{}", key, field, ex);
        }
        return false;
    }

    // ===============================Set=================================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public <T> Set<T> smembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception ex) {
            log.error("redisClient.smembers err, key:{}, ex:{}", key, ex);
        }
        return null;
    }


    public boolean sismember(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception ex) {
            log.error("redisClient.sismember err, key:{}, value:{}, ex:{}", key, value, ex);
            return false;
        }
    }


    public long sadd(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception ex) {
            log.error("redisClient.sadd err, key:{}, value:{}, ex:{}", key, JSON.toJSONString(values), ex);
        }
        return 0;
    }


    public long scard(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception ex) {
            log.error("redisClient.scard err, key:{}, ex:{}", key, ex);
        }
        return 0;
    }


    public long srem(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception ex) {
            log.error("redisClient.srem err, key:{}, value:{}, ex:{}", key, values, ex);
        }
        return 0;
    }

    // ===============================list=================================


    public <T> List<T> lrange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception ex) {
            log.error("redisClient.lrange err, key:{}, start:{}, end:{}, ex:{}", key, start, end, ex);
        }
        return null;
    }


    public long llen(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception ex) {
            log.error("redisClient.llen err, key:{}, ex:{}", key, ex);
        }
        return 0;
    }


    public Object lindex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception ex) {
            log.error("redisClient.lindex err, key:{}, index:{}, ex:{}", key, index, ex);
        }
        return null;
    }


    public <T> boolean rpush(String key, T value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception ex) {
            log.error("redisClient.rpush err, key:{}, value:{}, ex:{}", key, value, ex);
        }
        return false;
    }


    public <T> boolean rpush(String key, List<T> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception ex) {
            log.error("redisClient.rpush err, key:{}, value:{}, ex:{}", key, JSON.toJSONString(value), ex);
        }
        return false;
    }


    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    public <T> boolean lset(String key, long index, T value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception ex) {
            log.error("redisClient.lset err, key:{}, value:{}, ex:{}", key, JSON.toJSONString(value), ex);
        }
        return false;
    }


    public <T> long lrem(String key, long count, T value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception ex) {
            log.error("redisClient.lrem err, key:{}, count:{}, value:{}, ex:{}", key, count, JSON.toJSONString(value), ex);
        }
        return 0;
    }

    /**------------------zSet相关操作--------------------------------*/

    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     * @param value
     * @param score
     * @return
     */
    public boolean zadd(String key, String value, double score) {
        try {
            return redisTemplate.opsForZSet().add(key, value, score);
        } catch (Exception ex) {
            log.error("redisClient.lrem err, key:{}, value:{}, score:{}, ex:{}", key, value, score, ex);
        }
        return false;
    }

    public Long zadd(String key, Set<ZSetOperations.TypedTuple<String>> values) {
        try {
            return redisTemplate.opsForZSet().add(key, values);
        } catch (Exception ex) {
            log.error("redisClient.lrem err, key:{}, value:{}, ex:{}", key, JSON.toJSONString(values), ex);
        }
        return 0L;
    }


    public Long zrem(String key, Object... values) {
        try {
            return redisTemplate.opsForZSet().remove(key, values);
        } catch (Exception ex) {
            log.error("redisClient.zrem err, key:{}, value:{}, ex:{}", key, JSON.toJSONString(values), ex);
        }
        return 0L;
    }


    public Double zincrby(String key, String value, double delta) {
        try {
            return redisTemplate.opsForZSet().incrementScore(key, value, delta);
        } catch (Exception ex) {
            log.error("redisClient.zincrby err, key:{}, value:{}, delta:{}, ex:{}", key, value, delta, ex);
        }
        return 0D;
    }


    public Long zrank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().rank(key, value);
        } catch (Exception ex) {
            log.error("redisClient.zrank err, key:{}, value:{}, ex:{}", key, JSON.toJSONString(value), ex);
        }
        return 0L;
    }


    public Long zrevrank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().reverseRank(key, value);
        } catch (Exception ex) {
            log.error("redisClient.zrevrank err, key:{}, value:{}, ex:{}", key, JSON.toJSONString(value), ex);
        }
        return 0L;
    }


    public Set<String> zrange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception ex) {
            log.error("redisClient.zrange err, key:{}, start:{}, end:{}, ex:{}", key, start, end, ex);
        }
        return null;
    }


    public Set<ZSetOperations.TypedTuple<String>> zrangeWithScores(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
        } catch (Exception ex) {
            log.error("redisClient.zrangeWithScores err, key:{}, start:{}, end:{}, ex:{}", key, start, end, ex);
        }
        return null;
    }


    public Long zcount(String key, double min, double max) {
        try {
            return redisTemplate.opsForZSet().count(key, min, max);
        } catch (Exception ex) {
            log.error("redisClient.zcount err, key:{}, min:{}, max:{}, ex:{}", key, min, max, ex);
        }
        return null;
    }

    public Long zcard(String key) {
        try {
            return redisTemplate.opsForZSet().zCard(key);
        } catch (Exception ex) {
            log.error("redisClient.zcard err, key:{}, ex:{}", key, ex);
        }
        return null;
    }


    public Double zscore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception ex) {
            log.error("redisClient.zscore err, key:{}, value:{}, ex:{}", key, value, ex);
        }
        return null;
    }

    /**
     * 尝试获取分布式锁
     *
     * @param lockKey    锁
     * @param requestId  请求标识
     * @param expireTime 超期时间 单位秒
     * @return 是否获取成功
     */
    public boolean tryGetDistributedLock(String lockKey, String requestId, long expireTime) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
        } catch (Exception ex) {
            log.error("redisClient.tryGetDistributedLock err, lockKey:{}, requestId:{}, expireTime:{}, ex:{}", lockKey, requestId, expireTime, ex);
        }
        return false;
    }


    /**
     * 释放分布式锁
     *
     * @param lockKey   锁
     * @param requestId 请求标识
     * @return 是否释放成功
     */
    public boolean releaseDistributedLock(String lockKey, String requestId) {
        try {
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            RedisScript<Long> redisScript = RedisScript.of(script, Long.class);
            Long result = (Long) redisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
            return Objects.equals(UNLOCK_SUCCESS_RESULT, result);
        } catch (Exception ex) {
            log.error("redisClient releaseDistributedLock err, key:{}, requestId:{}, ex:{}", lockKey, requestId, ex);
        }
        return false;
    }



}
