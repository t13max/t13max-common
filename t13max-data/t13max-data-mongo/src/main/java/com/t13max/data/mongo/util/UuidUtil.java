package com.t13max.data.mongo.util;

import com.t13max.common.config.BaseConfig;
import com.t13max.common.run.Application;
import lombok.experimental.UtilityClass;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: t13max
 * @since: 13:57 2024/5/29
 */
@UtilityClass
public class UuidUtil {
    private final static long START_TIME = 1716912000000L;//2024-05-29 00:00:00

    private final static AtomicLong ID = new AtomicLong();

    private final static int MASK_12 = 0XFFF;//12位掩码

    private final static int INSTANCE_NO;

    static {
        BaseConfig config = Application.config();
        if (config != null) {
            INSTANCE_NO = Application.config().getInstanceNo();
        } else {
            INSTANCE_NO = 0;
            Log.MONGO.error("config为空, instanceNo为0");
        }
    }

    /**
     * 全局唯一
     * 后续优化一下 目前有那么一丢丢的可能就是一毫秒内获取超过4096个id就出事了
     *
     * @Author t13max
     * @Date 11:11 2024/4/16
     */
    public long getNextId() {
        long currentTime = System.currentTimeMillis() - START_TIME;
        return (currentTime << 23) + (INSTANCE_NO << 12) + (ID.getAndIncrement() & MASK_12);
    }

}
