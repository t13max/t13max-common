package com.t13max.rpc.proxy;

import org.apache.logging.log4j.util.Strings;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RPC接口的本地实现
 *
 * @Author t13max
 * @Date 16:24 2024/10/30
 */
public class RPCServerImplProxyHandler<T> implements InvocationHandler {

    private final T realObject;
    private final String toString;

    private final Map<String, String> metricMethodNameMap = new ConcurrentHashMap<>();

    public RPCServerImplProxyHandler(T realObject) {
        this.realObject = realObject;

        this.toString = getClass().getSimpleName() + "-proxy-" + UUID.randomUUID();
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("toString")) {
            return toString;
        } else if (method.getName().equals("equals")) {
            return proxy == args[0];
        } else if (method.getName().equals("hashCode")) {
            return toString.hashCode();
        }

        String metricLabelValue = metricMethodNameMap.get(method.getName());
        if (Strings.isBlank(metricLabelValue)) {
            metricLabelValue = realObject.getClass().getSimpleName() + "." + method.getName();
            metricMethodNameMap.put(method.getName(), metricLabelValue);
        }

        try {
            return method.invoke(realObject, args);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
        }
    }
}
