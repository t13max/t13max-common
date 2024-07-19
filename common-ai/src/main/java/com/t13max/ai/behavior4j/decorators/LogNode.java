package com.t13max.ai.behavior4j.decorators;


import com.t13max.ai.behavior4j.BTNode;
import lombok.extern.log4j.Log4j2;

import java.lang.reflect.InvocationTargetException;

/**
 * 日志节点
 *
 * @Author t13max
 * @Date 18:14 2024/5/17
 */
@Log4j2
public class LogNode<E> extends Decorator<E> {

    private String message;

    @Override
    protected void run() {
        log();
        super.run();
    }

    @Override
    protected void copy(BTNode<E> node) throws InvocationTargetException, IllegalAccessException {
        super.copy(node);
        LogNode<E> logNode = (LogNode<E>) node;
        logNode.message = message;
    }

    private void log() {
        log.info(message);
    }
}
