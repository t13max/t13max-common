package com.t13max.common.action;

/**
 * 任务名
 * 调用的模块需要写一个枚举 实现此接口
 *
 * @author: t13max
 * @since: 17:27 2024/7/19
 */
public interface IJobName {

    IJobName DEF = () -> "def";

    String getJobName();
}
