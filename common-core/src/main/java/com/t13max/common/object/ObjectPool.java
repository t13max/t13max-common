package com.t13max.common.object;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 后续优化 自动增减 参考线程池?
 *
 * @author: t13max
 * @since: 16:28 2024/7/23
 */
public class ObjectPool<T extends IPooledObject> {

    private BlockingQueue<T> blockingQueue;
    private int maxSize;
    private ObjectFactory<T> factory;

    public ObjectPool(int maxSize, ObjectFactory<T> factory) {
        this.maxSize = maxSize;
        this.factory = factory;
        blockingQueue = new LinkedBlockingQueue<>(maxSize);
        // 初始化池
        /*for (int i = 0; i < maxSize; i++) {
            blockingQueue.offer(factory.create());
        }*/
    }

    // 从池中借用对象
    public T borrowObject() {
        T object = blockingQueue.poll();
        if (object == null) {
            object = factory.create();
        }
        object.borrowObject();
        return object;
    }

    // 返还对象到池中
    public void returnObject(T obj) {
        if (obj != null) {
            obj.returnObject();
            blockingQueue.offer(obj);
        }
    }

}
