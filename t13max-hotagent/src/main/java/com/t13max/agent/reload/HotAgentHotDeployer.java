package com.t13max.agent.reload;

import java.lang.instrument.Instrumentation;

/**
 * @author: t13max
 * @since: 15:42 2024/8/12
 */
public class HotAgentHotDeployer extends HotDeployer {

    public HotAgentHotDeployer(Result var1, Instrumentation var2) {
        super(var1, var2);
    }

    protected String getHotDeployJarPath() {
        return "hotagent.jar";
    }

    protected boolean isRequired() {
        return false;
    }
}
