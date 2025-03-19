package com.t13max.agent.deployer;

import com.t13max.agent.wrap.Result;

import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * @author: t13max
 * @since: 15:43 2024/8/12
 */
public class HotDeployHotDeployer extends HotDeployer{

    public HotDeployHotDeployer(Result result, Instrumentation instrumentation) {
        super(result, instrumentation);
    }

    protected String getHotDeployJarPath() {
        return "hotdeploy.jar";
    }

    protected List<String> getDelayExecClassNames() {
        return List.of("com.t13max.agent.HotAgentSupport");
    }

}
