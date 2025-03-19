package com.t13max.agent.deployer;

import com.t13max.agent.wrap.Result;

import java.lang.instrument.Instrumentation;

/**
 * @author: t13max
 * @since: 15:42 2024/8/12
 */
public class HotAgentHotDeployer extends HotDeployer {

    public HotAgentHotDeployer(Result result, Instrumentation instrumentation) {
        super(result, instrumentation);
    }

    protected String getHotDeployJarPath() {
        return "hotagent.jar";
    }

    protected boolean isRequired() {
        return false;
    }
}
