package com.t13max.bot.state;

import java.lang.annotation.*;

/**
 * 状态入口
 * 标记一个状态是入口状态
 *
 * @Author t13max
 * @Date 17:56 2024/8/14
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnterState {
}
