package com.t13max.agent.bean;

import com.t13max.agent.deployer.HotAgentHotDeployer;
import com.t13max.agent.deployer.HotDeployHotDeployer;
import com.t13max.agent.deployer.HotDeployer;
import com.t13max.agent.wrap.*;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.List;

/**
 * Reload对象
 *
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
        //两种模式 分别创建调用
        List<HotDeployer> deployerList = Arrays.asList(new HotAgentHotDeployer(result, this.instrumentation), new HotDeployHotDeployer(result, this.instrumentation));
        for (HotDeployer hotDeployer : deployerList) {
            if (!hotDeployer.exec().isSuccess()) {
                break;
            }
        }
        return result.getMsgBuilder();
    }
}
