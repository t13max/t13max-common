package com.t13max.data.redis.consts;

/**
 * @author t13max
 * @since 14:39 2024/11/5
 */
public interface Const {
    String SPLITTER = ":";
    String BASE_PREFIX = "event" + SPLITTER + "channel" + SPLITTER;

    String GLOBAL_EVENT_CHANNEL_PREFIX = BASE_PREFIX + "global";
    String SERVICE_EVENT_CHANNEL_PREFIX = BASE_PREFIX + "service";
    String INSTANCE_EVENT_CHANNEL_PREFIX = BASE_PREFIX + "instance";
}
