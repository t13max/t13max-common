package com.t13max.agent.reload;

import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * @author: t13max
 * @since: 15:43 2024/8/12
 */
public class HotDeployHotDeployer extends HotDeployer{
    public HotDeployHotDeployer(Result var1, Instrumentation var2) {
        super(var1, var2);
    }

    protected String getHotDeployJarPath() {
        return "hotdeploy.jar";
    }

    protected List<String> getDelayExecClassNames() {
        return List.of("com.t13max.agent.HotAgentSupport");
    }

}
