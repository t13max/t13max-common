package com.t13max.common.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 日志工具类
 *
 * @author: t13max
 * @since: 17:35 2024/7/19
 */
public class Log {
    //玩家业务线程池
    public static Logger action = LogManager.getLogger("ACTION");
    public static Logger manager = LogManager.getLogger("MANAGER");
}
