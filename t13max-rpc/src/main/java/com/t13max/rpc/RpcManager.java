package com.t13max.rpc;

import com.alipay.sofa.rpc.common.RpcConfigs;
import com.alipay.sofa.rpc.common.RpcOptions;
import com.alipay.sofa.rpc.config.ConsumerConfig;
import com.alipay.sofa.rpc.config.ProviderConfig;
import com.alipay.sofa.rpc.config.RegistryConfig;
import com.alipay.sofa.rpc.config.ServerConfig;
import com.t13max.common.config.RpcConfig;
import com.t13max.common.exception.CommonException;
import com.t13max.common.manager.ManagerBase;
import com.t13max.common.run.Application;
import com.t13max.rpc.anno.RpcImpl;
import com.t13max.rpc.anno.RpcInterface;
import com.t13max.rpc.fail.FailSafe;
import com.t13max.rpc.fail.FailSafeStatus;
import com.t13max.rpc.fail.Status;
import com.t13max.rpc.proxy.RPCServerImplProxyHandler;
import com.t13max.rpc.util.Log;
import com.t13max.util.OSUtils;
import com.t13max.util.PackageUtil;
import com.t13max.util.SocketUtils;
import net.jodah.failsafe.CircuitBreaker;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.alipay.remoting.Configs.NETTY_BUFFER_HIGH_WATERMARK;
import static com.alipay.remoting.Configs.NETTY_BUFFER_LOW_WATERMARK;


/**
 * rpc 管理器
 *
 * @author t13max
 * @since 15:49 2024/9/10
 */
public class RpcManager extends ManagerBase {

    protected static final String PROTOCOL = "bolt";
    protected static final String SERIALIZATION = "hessian2";
    // 随机分配rpc端口重试次数
    protected static final int MAX_GET_RPC_PORT_TRY_TIMES = 100;

    protected final Map<Class<?>, Object> serviceMap = new HashMap<>();
    protected Map<Class<?>, Object> localRPCImplMap = new HashMap<>();
    protected List<ProviderConfig<?>> providerConfigs = new LinkedList<>();
    protected Map<Method, FailSafe> failSafeMapByMethod = new HashMap<>();
    //protected Map<Method, RateLimiter> methodRateLimiterMap = new HashMap<>();
    protected static final Map<Method, String> rpcMethodShortNameMap = new ConcurrentHashMap<>();
    private RegistryConfig registryConfig;
    protected ServerConfig serverConfig;

    protected volatile boolean exported;
    protected volatile int rpcServerPort = -1;

    @Override
    protected void init() {

        initRpc();

        RpcConfig rpcConfig = Application.config().getRpc();
        if (!rpcConfig.isOpen()) {
            Log.RPC.info("未开启RPC");
            return;
        }
        registryConfig = new RegistryConfig().setProtocol(rpcConfig.getRegistryProtocol()).setAddress(rpcConfig.getAddress());
        try {
            Set<Class<?>> classSet = PackageUtil.scan("com.t13max");
            //创建实例
            for (Class<?> clazz : classSet) {
                RpcImpl annotation = clazz.getAnnotation(RpcImpl.class);
                if (annotation == null) {
                    continue;
                }
                // 创建实例
                Object inst = clazz.getDeclaredConstructor().newInstance();
                Class<?>[] interfaces = inst.getClass().getInterfaces();
                Class<?> theInterface = interfaces[0];
                Object proxy = Proxy
                        .newProxyInstance(inst.getClass().getClassLoader(), new Class[]{theInterface},
                                new RPCServerImplProxyHandler<>(inst));
                ProviderConfig<?> providerConfig = new ProviderConfig<>()
                        .setInterfaceId(theInterface.getName())
                        .setRef(proxy)
                        .setRegistry(registryConfig);
                providerConfigs.add(providerConfig);
                localRPCImplMap.put(theInterface, proxy);
                Log.RPC.info("register rpc impl :{}", theInterface.getName());
            }

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new CommonException(e);
        }

        //对外提供服务
        exportService();
    }

    private void initRpc() {
        // 需要关掉SofaRPC的shutdown钩子，不然在正常shutdown时rpc会不可用
        RpcConfigs.putValue(RpcOptions.JVM_SHUTDOWN_HOOK, false);
        // 序列化方式 支持 hessian2,json
        RpcConfigs.putValue(RpcOptions.DEFAULT_SERIALIZATION, "hessian2");
        // 开压缩
        RpcConfigs.putValue(RpcOptions.COMPRESS_OPEN, true);
        // 客户端在Linux开EPoll
        RpcConfigs.putValue(RpcOptions.TRANSPORT_USE_EPOLL, OSUtils.isUnix());
        // 服务器在Linux开EPoll
        RpcConfigs.putValue(RpcOptions.SERVER_EPOLL, OSUtils.isUnix());
        // 去除客户端接口重复引用次数限制
        RpcConfigs.putValue(RpcOptions.CONSUMER_REPEATED_REFERENCE_LIMIT, -1);

        // 传输缓冲区参数设置
        // 默认8192
        RpcConfigs.putValue(RpcOptions.TRANSPORT_BUFFER_SIZE, 16 * 1024);
        // 默认1024
        RpcConfigs.putValue(RpcOptions.TRANSPORT_BUFFER_MIN, 8 * 1024);
        // 默认32768
        RpcConfigs.putValue(RpcOptions.TRANSPORT_BUFFER_MAX, 128 * 1024);
        // 默认8388608
        RpcConfigs.putValue(RpcOptions.TRANSPORT_PAYLOAD_MAX, 4 * 1024 * 1024);

        System.setProperty(NETTY_BUFFER_LOW_WATERMARK, Integer.toString(1 * 1024 * 1024));
        System.setProperty(NETTY_BUFFER_HIGH_WATERMARK, Integer.toString(4 * 1024 * 1024));
    }

    @Override
    protected void onShutdown() {
        if (Objects.nonNull(serverConfig)) {
            serverConfig.destroy();
        }
    }

    public <T> T get(Class<T> remoteInterface) {
        Object instance = serviceMap.get(remoteInterface);
        if (instance == null) {
            synchronized (remoteInterface) {
                instance = serviceMap.get(remoteInterface);
                if (instance == null) {

                    RpcConfig rpcConfig = Application.config().getRpc();
                    if (rpcConfig.isInJVM()) {
                        instance = localRPCImplMap.get(remoteInterface);
                    }

                    if (Objects.isNull(instance)) {
                        instance = buildService(remoteInterface);
                    }

                    if (rpcConfig.isFailsafe()) {
                        instance = Proxy
                                .newProxyInstance(remoteInterface.getClassLoader(), new Class[]{remoteInterface},
                                        new RPCInterfaceProxyHandler(remoteInterface, instance));
                    }
                    serviceMap.put(remoteInterface, instance);
                }
            }
        }
        return (T) instance;
    }

    public Status getStatus() {
        return generateStatus();
    }

    private Status generateStatus() {
        Status status = new Status();
        Map<String, FailSafeStatus> failSafeStatusMap = new TreeMap<>();
        for (Map.Entry<Method, FailSafe> entry : failSafeMapByMethod.entrySet()) {
            String key = rpcMethodShortNameMap.get(entry.getKey());
            FailSafe failSafe = entry.getValue();
            failSafeStatusMap
                    .put(key, new FailSafeStatus(failSafe));
        }
        status.setFailSafeStatusMap(failSafeStatusMap);
        return status;
    }

    private <T> T buildService(Class<T> clazz) {
        RpcConfig rpcConfig = Application.config().getRpc();
        ConsumerConfig<?> consumerConfig = new ConsumerConfig<>()
                .setConnectionNum(rpcConfig.getCores())
                .setConcurrents(rpcConfig.getMethodConcurrents())
                .setInterfaceId(clazz.getName())
                .setProtocol(PROTOCOL)
//            .setSerialization(SERIALIZATION)
                .setTimeout(rpcConfig.getCallTimeout())
                .setRegistry(registryConfig);
        return (T) consumerConfig.refer();
    }

    protected void exportService() {
        if (exported) {
            return;
        }
        exported = true;

        rpcServerPort = -1;

        int tryTimes = MAX_GET_RPC_PORT_TRY_TIMES;
        while (tryTimes-- > 0) {
            int port = SocketUtils.randomPort();
            try {
                int coreThreads = 16;

                ServerConfig serverConfig = new ServerConfig()
                        .setProtocol(PROTOCOL)
                        .setPort(port)
                        .setQueues(0xffff)
                        .setCoreThreads(coreThreads)
                        .setMaxThreads(coreThreads * 2)
                        .setIoThreads(coreThreads * 2)
//                    .setKeepAlive(true)
                        .setDaemon(false);
                for (ProviderConfig providerConfig : providerConfigs) {
                    if (!providerConfig.getServer().isEmpty()) {
                        providerConfig.getServer().clear();
                    }
                    providerConfig.setServer(serverConfig);
                    providerConfig.export();
                }
                rpcServerPort = port;
                this.serverConfig = serverConfig;
                break;
            } catch (Exception e) {
                if (!e.getMessage().contains("Failed to start bolt server, see more detail from bolt log.")
                        && !e.getMessage().contains("Failed to start the Server")) {
                    e.printStackTrace();
                } else {
                    Log.RPC.info("SofaRpcHelper find port:{} skipped", port);
                }
            }
        }

        if (rpcServerPort == -1) {
            Log.RPC.error("SofaRpcHelper find port failed");
            System.exit(-99);
        } else {
            Log.RPC.info("SofaRpcHelper port:{}", rpcServerPort);
        }
    }

    private class RPCInterfaceProxyHandler<T> implements InvocationHandler {

        private Class rpcInterface;
        private Object realObject;
        private String toString;

        // 只根据rpc接口实例化的对象,用来获得default接口返回值, 用作熔断时返回给业务
        private T fallbacker;

        public RPCInterfaceProxyHandler(Class<T> rpcInterface, Object realObject) {
            RpcConfig rpcConfig = Application.config().getRpc();
            this.rpcInterface = rpcInterface;
            this.realObject = realObject;
            this.toString = getClass().getSimpleName() + "-" + rpcInterface.getSimpleName() + "-proxy-" + UUID.randomUUID();
            Method[] methods = rpcInterface.getDeclaredMethods();
            for (Method method : methods) {
                String methodShortName = method.toGenericString().split(" ")[3];
                rpcMethodShortNameMap.put(method, methodShortName);
                if (rpcConfig.isFailsafe()) {
                    fallbacker = instanceInterface(rpcInterface);
                    RpcInterface theInterface = rpcInterface.getAnnotation(RpcInterface.class);
                    CircuitBreaker breaker = new CircuitBreaker()
                            .withFailureThreshold(theInterface.failureThresholdFailures(),
                                    theInterface.failureThresholdExecutions())
                            .withSuccessThreshold(theInterface.successThresholdSuccess(),
                                    theInterface.successThresholdExecutions())
                            .withDelay(Duration.ofSeconds(theInterface.breakerDelay()))
                            .onHalfOpen(() -> Log.RPC.info("rpc:{} method:{} halfOpen", rpcInterface.getSimpleName(), method.getName()))
                            .onClose(() -> Log.RPC.info("rpc:{} method:{} close", rpcInterface.getSimpleName(), method.getName()))
                            .onOpen(() -> Log.RPC.info("rpc:{} method:{} open", rpcInterface.getSimpleName(), method.getName()));
                    failSafeMapByMethod.put(method, new FailSafe(methodShortName, rpcInterface, method, breaker));
                }

                /*if (rpcConfiguration.isRateLimit()) {
                    RPCRateLimiter rateLimiter = method.getAnnotation(RPCRateLimiter.class);
                    methodRateLimiterMap.put(method, RateLimiter.create(
                            Objects.isNull(rateLimiter) ?
                                    NirvanaRPCRateLimiter.DEFAULT_RATE
                                    : rateLimiter.rateLimitPerSeconds()));
                }*/
            }

        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getName().equals("toString")) {
                return toString;
            } else if (method.getName().equals("equals")) {
                return proxy == args[0];
            } else if (method.getName().equals("hashCode")) {
                return toString.hashCode();
            }
            RpcConfig rpcConfig = Application.config().getRpc();
            if (rpcConfig.isFailsafe()) {
                FailSafe failsafe = failSafeMapByMethod.get(method);
                if (!failsafe.isInitedResult()) {
                    // 初始化fallback结果
                    try {
                        failsafe.setFallBackResult(method.invoke(fallbacker, args));
                    } catch (Exception e) {
//                        e.printStackTrace();
                    } finally {
                        failsafe.setInitedResult(true);
                    }
                }
                return failsafe.execute(() -> call(method, args), args);
            } else {
                return call(method, args);
            }
        }

        private Object call(Method method, Object[] args) throws Exception {
            return invoke(method, args);
        }

        private Object invoke(Method method, Object[] args) throws Exception {
            /*if (rpcConfiguration.isRateLimit()) {
                methodRateLimiterMap.get(method).acquire();
            }*/
            try {
                return method.invoke(realObject, args);
            } catch (Exception e) {
                e.printStackTrace();
                Log.RPC.error("exception : name:{} args:{}", method.getName(), args);
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        private <T> T instanceInterface(Class<T> clazz) {
            return (T) Proxy.newProxyInstance(
                    Thread.currentThread().getContextClassLoader(),
                    new Class[]{clazz},
                    (proxy, method, args) -> {
                        if (!method.isDefault()) {
                            return null;
                        }
                        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                                .getDeclaredConstructor(Class.class);
                        constructor.setAccessible(true);
                        return constructor.newInstance(clazz)
                                .in(clazz)
                                .unreflectSpecial(method, clazz)
                                .bindTo(proxy)
                                .invokeWithArguments(args);
                    }
            );
        }
    }
}
