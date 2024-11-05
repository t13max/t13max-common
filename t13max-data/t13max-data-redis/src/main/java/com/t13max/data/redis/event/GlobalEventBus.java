package com.t13max.data.redis.event;

import com.t13max.common.event.GameEventBus;
import com.t13max.common.event.IEvent;
import com.t13max.common.run.Application;
import com.t13max.data.redis.RedisManager;
import com.t13max.data.redis.consts.Const;
import com.t13max.data.redis.utils.Log;
import org.redisson.api.RTopic;
import org.redisson.api.listener.MessageListener;

import java.util.Objects;


/**
 * 全局事件总线 可跨进程
 *
 * @author t13max
 * @since 14:33 2024/11/5
 */
public class GlobalEventBus extends GameEventBus implements MessageListener<IEvent> {

    private String sameServiceChannel;

    @Override
    protected void onShutdown() {
        super.onShutdown();
        removeListener();
    }

    @Override
    protected void init() {
        super.init();
        addListener();
    }

    public void postEvent(Region region, IEvent event) {
        sendRegionMessage(region, event);
    }

    public void postEvent(String instanceId, IEvent event) {
        if (instanceId.equals(Application.config().getInstanceName())) {
            // 如果给自己实例发,直接进程内广播
            postEvent(Region.IN_PROCESS, event);
        } else {
            getTopic(this.join(Const.INSTANCE_EVENT_CHANNEL_PREFIX, instanceId)).publishAsync(event);
        }
    }

    private void sendRegionMessage(Region region, IEvent event) {

        if (region == Region.IN_PROCESS) {
            postEvent(event);
            return;
        }

        String topic = null;

        switch (region) {
            case GLOBAL:
                topic = Const.GLOBAL_EVENT_CHANNEL_PREFIX;
                break;
            case SAME_SERVICE:
                if (Objects.isNull(sameServiceChannel)) {
                    sameServiceChannel = join(Const.SERVICE_EVENT_CHANNEL_PREFIX, Application.config().getInstanceName());
                }
                topic = sameServiceChannel;
                break;
            default:
                break;
        }

        if (Objects.isNull(topic)) {
            Log.REDIS.error("sendRegionMessage region:{} topic is null", region);
            return;
        }

        getTopic(topic).publishAsync(event);
    }

    private RTopic getTopic(String topic) {
        return RedisManager.inst().getTopic(topic);
    }

    private String join(String prefix, String name) {
        return prefix + Const.SPLITTER + name;
    }

    /**
     * 添加监听
     *
     * @Author t13max
     * @Date 14:54 2024/11/5
     */
    private void addListener() {
        RedisManager.inst().getTopic(Const.GLOBAL_EVENT_CHANNEL_PREFIX).addListener(IEvent.class, this);
        RedisManager.inst().getTopic(join(Const.SERVICE_EVENT_CHANNEL_PREFIX, Application.config().getServiceName())).addListenerAsync(IEvent.class, this);
        RedisManager.inst().getTopic(join(Const.INSTANCE_EVENT_CHANNEL_PREFIX, Application.config().getServiceName())).addListenerAsync(IEvent.class, this);
    }

    /**
     * 移除订阅
     *
     * @Author t13max
     * @Date 14:54 2024/11/5
     */
    private void removeListener() {
        RedisManager.inst().getTopic(Const.GLOBAL_EVENT_CHANNEL_PREFIX).removeListener(this);
        RedisManager.inst().getTopic(join(Const.SERVICE_EVENT_CHANNEL_PREFIX, Application.config().getServiceName())).removeListener(this);
        RedisManager.inst().getTopic(join(Const.INSTANCE_EVENT_CHANNEL_PREFIX, Application.config().getInstanceName())).removeListener(this);
    }

    @Override
    public void onMessage(CharSequence channel, IEvent msg) {
        postEvent(msg);
    }
}
