package com.example.demo.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用于记录本地和远程版本缓存的版本号，解决String对象加锁问题
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class VersionObject {

    /**
     * 版本号
     */
    private String version;
}
