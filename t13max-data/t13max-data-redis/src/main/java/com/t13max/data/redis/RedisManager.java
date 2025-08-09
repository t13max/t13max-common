package com.t13max.data.redis;


import com.t13max.common.config.BaseConfig;
import com.t13max.common.config.RedisConfig;
import com.t13max.common.config.SingleRedissonConfig;
import com.t13max.common.manager.ManagerBase;
import com.t13max.common.run.Application;
import com.t13max.data.redis.utils.IRedisUtils;
import com.t13max.data.redis.utils.Log;
import com.t13max.data.redis.utils.RedissonUtils;
import com.t13max.data.redis.utils.SimpleRedisUtils;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.LinkedList;
import java.util.List;

/**
 * @author t13max
 * @since 15:10 2024/10/30
 */
public class RedisManager extends ManagerBase {

    private IRedisUtils redisUtils;

    public static RedisManager inst() {
        return inst(RedisManager.class);
    }

    @Override
    protected void init() {
        BaseConfig baseConfig = Application.config();
        RedisConfig redis = baseConfig.getRedis();
        if (redis.isSimple()) {
            redisUtils = new SimpleRedisUtils();
            Log.REDIS.info("Redis使用简易版");
        } else {
            List<SingleRedissonConfig> singleRedissonConfig = redis.getSingleRedissonConfig();
            List<RedissonClient> redissonClientList = new LinkedList<>();
            for (SingleRedissonConfig redissonConfig : singleRedissonConfig) {
                Config config = createConfig(redissonConfig);
                RedissonClient redissonClient = Redisson.create(config);
                redissonClientList.add(redissonClient);
            }
            //传入RedisClient
            redisUtils = new RedissonUtils(redissonClientList);
            Log.REDIS.info("Redis使用正式版");
        }
    }

    private Config createConfig(SingleRedissonConfig singleRedissonConfig) {
        Config config = new Config();
        SingleServerConfig serverConfig = config.useSingleServer()
                .setAddress(singleRedissonConfig.getAddress())
                .setIdleConnectionTimeout(singleRedissonConfig.getIdleConnectionTimeout())
                .setConnectTimeout(singleRedissonConfig.getConnectTimeout())
                .setTimeout(singleRedissonConfig.getTimeout())
                .setRetryAttempts(singleRedissonConfig.getRetryAttempts())
                .setRetryInterval(singleRedissonConfig.getRetryInterval())
                .setSubscriptionsPerConnection(singleRedissonConfig.getSubscriptionsPerConnection())
                .setSubscriptionConnectionMinimumIdleSize(singleRedissonConfig.getSubscriptionConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(singleRedissonConfig.getSubscriptionConnectionPoolSize())
                .setDatabase(singleRedissonConfig.getDatabase())
                .setConnectionPoolSize(singleRedissonConfig.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(singleRedissonConfig.getConnectionMinimumIdleSize())
                .setPingConnectionInterval(300000)
                .setKeepAlive(true)
                .setTcpNoDelay(true);
        String password = singleRedissonConfig.getPassword();
        if (password != null && !password.isBlank()) {
            serverConfig.setPassword(password);
        }
        config.setCodec(JsonJacksonCodec.INSTANCE);
        return config;
    }

    @Override
    protected void onShutdown() {
        redisUtils.closeRedisClient();
    }

    public IRedisUtils redisUtils() {
        return redisUtils;
    }

    public <T> RBucket<T> getBucket(String key) {
        return redisUtils.getBucket(key);
    }

    public <K, V> RMap<K, V> getMap(String key) {
        return redisUtils.getMap(key);
    }

    public <K, V> RMap<K, V> getDBMap(String key) {
        return redisUtils.getDBMap(key);
    }

    public <V> RSetCache<V> getSetCache(String key) {
        return redisUtils.getSetCache(key);
    }

    public <V> RSortedSet<V> getSortedSet(String key) {
        return redisUtils.getSortedSet(key);
    }

    public <V> RSet<V> getSet(String key) {
        return redisUtils.getSet(key);
    }

    public <K, V> RSetMultimap<K, V> getSetMultimap(String key) {
        return redisUtils.getSetMultimap(key);
    }

    public <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String key) {
        return redisUtils.getSetMultimapCache(key);
    }

    public <V> RList<V> getList(String key) {
        return redisUtils.getList(key);
    }

    public <K, V> RListMultimap<K, V> getListMultimap(String key) {
        return redisUtils.getListMultimap(key);
    }

    public <V> RQueue<V> getQueue(String key) {
        return redisUtils.getQueue(key);
    }

    public <V> RBlockingQueue<V> getBlockingQueue(String key) {
        return redisUtils.getBlockingQueue(key);
    }

    public <V> RDeque<V> getDeque(String key) {
        return redisUtils.getDeque(key);
    }

    public RLock getLock(String key) {
        return redisUtils.getLock(key);
    }

    public RLock getMultiLock(RLock... rLock) {
        return redisUtils.getMultiLock(rLock);
    }

    public RAtomicLong getAtomicLong(String key) {
        return redisUtils.getAtomicLong(key);
    }

    public RCountDownLatch getCountDownLatch(String key) {
        return redisUtils.getCountDownLatch(key);
    }

    public RTopic getTopic(String key) {
        return redisUtils.getTopic(key);
    }

    public RTopic getTopic(String key, Codec codec) {
        return redisUtils.getTopic(key, codec);
    }

    public <V> RGeo<V> getGeo(String key) {
        return redisUtils.getGeo(key);
    }

    public <V> RScoredSortedSet<V> getScoredSortedSet(String key) {
        return redisUtils.getScoredSortedSet(key);
    }

    public RReadWriteLock getReadWriteLock(String key) {
        return redisUtils.getReadWriteLock(key);
    }

    public RBatch getBatch(String className) {
        return redisUtils.getBatch(className);
    }

    public BatchResult<?> getBatchExecute(RBatch rBatch) {
        return redisUtils.getBatchExecute(rBatch);
    }
}
