package com.t13max.ai.behavior4j.attachments;

/**
 * 前置
 *
 * @Author t13max
 * @Date 18:34 2024/5/17
 */
public abstract class PreActionNode<E> extends AttachmentNode<E> {

    @Override
    public abstract boolean preCondition();

    @Override
    public final void effect() {
    }
}
