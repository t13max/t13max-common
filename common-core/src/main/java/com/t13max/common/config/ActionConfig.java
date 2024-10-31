package com.t13max.common.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * 业务线程池配置
 *
 * @author: t13max
 * @since: 20:11 2024/5/23
 */
@Getter
@Setter(AccessLevel.PRIVATE)
public class ActionConfig {

    private int core;

    private int max;

    private String name;
}
