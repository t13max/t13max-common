package com.t13max.ai.behavior4j.attachments;

/**
 * 后置
 *
 * @Author t13max
 * @Date 18:34 2024/5/17
 */
public abstract class PostActionNode<E> extends AttachmentNode<E> {

    @Override
    public final boolean preCondition() {
        return true;
    }

    @Override
    public void effect() {
        doEffect();
    }

    protected abstract void doEffect();
}
