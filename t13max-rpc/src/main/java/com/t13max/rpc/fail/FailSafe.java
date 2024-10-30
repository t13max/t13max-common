package com.t13max.rpc.fail;


import lombok.Getter;
import lombok.Setter;
import net.jodah.failsafe.*;
import net.jodah.failsafe.function.CheckedSupplier;

import java.lang.reflect.Method;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * RPC保护
 *
 * @Author t13max
 * @Date 17:24 2024/10/30
 */
public class FailSafe<T> {

    public enum Counter {
        SUCCESS,
        FAILURE,
        RETRY,
    }

    private String rpcMethodShortName;

    private Map<Counter, LongAdder> counterMap;

    private Class rpcInterface;

    private Method rpcMethod;

    private FailsafeExecutor<T> executor;

    @Setter
    @Getter
    private T fallBackResult;
    private CircuitBreaker breaker;

    @Getter
    @Setter
    private boolean initedResult;

    public FailSafe(String rpcMethodShortName, Class rpcInterface, Method rpcMethod, CircuitBreaker breaker) {
        this.rpcMethodShortName = rpcMethodShortName;
        this.rpcInterface = rpcInterface;
        this.rpcMethod = rpcMethod;
        counterMap = new EnumMap<>((Class) Counter.class);
        for (Counter counter : Counter.values()) {
            counterMap.put(counter, new LongAdder());
        }
        this.breaker = breaker;
        executor = Failsafe.with(Fallback.of(() -> fallBackResult), retryPolicy, breaker);
    }

    private void increment(Counter counter) {
        counterMap.get(counter).increment();
    }

    private RetryPolicy<T> retryPolicy = new RetryPolicy<T>()
            .withMaxRetries(0)
            .onSuccess(p -> increment(Counter.SUCCESS))
//        .onFailure(p -> increment(Counter.FAILURE))
            .onRetry(e -> increment(Counter.RETRY))
            .handle(Throwable.class);

    @SuppressWarnings("unchecked")
    public T execute(CheckedSupplier supplier, Object[] args) {
        executor.onFailure(p -> {
            increment(Counter.FAILURE);
        });
        return (T) executor.get(supplier);
    }

    public long getTimes(Counter counter) {
        return counterMap.get(counter).longValue();
    }

    public CircuitBreaker.State getState() {
        return breaker.getState();
    }
}
