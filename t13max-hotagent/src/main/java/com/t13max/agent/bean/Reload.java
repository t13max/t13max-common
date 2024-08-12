package com.t13max.agent.bean;

import com.t13max.agent.reload.*;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.List;

/**
 * @author: t13max
 * @since: 15:40 2024/8/12
 */
public class Reload implements IReload {

    private final Instrumentation instrumentation;

    public Reload(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    @Override
    public String reload() {
        Result result = new Result();
        List<HotDeployer> deployerList = Arrays.asList(new HotAgentHotDeployer(result, this.instrumentation), new HotDeployHotDeployer(result, this.instrumentation));
        for (HotDeployer hotDeployer : deployerList) {
            if (!hotDeployer.exec().isSuccess()) {
                break;
            }
        }
        return result.getMsgBuilder();
    }
}
