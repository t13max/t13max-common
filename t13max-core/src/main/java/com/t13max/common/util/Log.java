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
    public final static Logger APP = LogManager.getLogger("APP");
    public final static Logger ACTION = LogManager.getLogger("ACTION");
    public final static Logger MANAGER = LogManager.getLogger("MANAGER");
    public final static Logger MSG = LogManager.getLogger("MESSAGE");
    public final static Logger TEMPLATE = LogManager.getLogger("TEMPLATE");
}
