package com.example.demo.cache;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.convert.DurationStyle;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class CacheSupport {

    /**
     * 校验规则：获取时间
     */
    private static final String REGEX_STR = "^.*\\#\\d+[a-zA-Z]{0,2}$";

    /**
     * 分隔符
     */
    private static final String SPLITTER = "#";

    /**
     * 默认过期时间，未设置过期时间时生效
     */
    public static final Duration DEFAULT_TTL = Duration.ofDays(1L);

    /**
     * 缓存过期时间随机范围
     */
    protected static final int CACHE_RANDOM_RANGE = 11;


    /**
     * 解析名称和过期时间
     *
     * @return
     */
    public static Pair<String, Duration> getNameAndDefaultTimeOut(String name) {
        String cacheName = name;
        Duration ttl = null;
        //是否按照指定的格式
        if (Pattern.matches(REGEX_STR, name)) {
            List<String> keyList = Arrays.asList(name.split(SPLITTER));
            //获取键值
            cacheName = keyList.get(0);
            //获取TTL 执行时间
            ttl = DurationStyle.detectAndParse(keyList.get(1), ChronoUnit.SECONDS);
        }
        return ImmutablePair.of(cacheName, ttl);
    }


    public static Duration plusRandomSeconds(Duration expireTime) {
        Objects.requireNonNull(expireTime);
        //为了防止雪崩效应，添加0-10的随机数
        int randomInt = ThreadLocalRandom.current().nextInt(CACHE_RANDOM_RANGE);
        return expireTime.plusSeconds(randomInt);
    }
}
