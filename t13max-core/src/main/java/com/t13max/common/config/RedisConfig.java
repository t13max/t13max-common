package com.t13max.common.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author: t13max
 * @since: 19:09 2024/5/23
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class RedisConfig {

    private boolean simple = true;

    private List<SingleRedissonConfig> singleRedissonConfig;

}
