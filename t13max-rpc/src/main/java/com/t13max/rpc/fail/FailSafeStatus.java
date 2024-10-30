package com.t13max.rpc.fail;

import lombok.Data;
import net.jodah.failsafe.CircuitBreaker;

/**
 * @Author: zl1030
 * @Date: 2019/4/24
 */
@Data
public class FailSafeStatus {

    private CircuitBreaker.State state;
    private long success;
    private long failure;
    private long retry;

    public FailSafeStatus(FailSafe failSafe) {
        state = failSafe.getState();
        success = failSafe.getTimes(FailSafe.Counter.SUCCESS);
        failure = failSafe.getTimes(FailSafe.Counter.FAILURE);
        retry = failSafe.getTimes(FailSafe.Counter.RETRY);
    }

}
