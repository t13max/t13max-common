package com.t13max.template.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 日志工具类
 *
 * @author: t13max
 * @since: 18:21 2024/7/19
 */
public class Log {
    public static Logger def = LogManager.getLogger("DEF");
    public static Logger template = LogManager.getLogger("TEMPLATE");

}
