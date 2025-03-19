package com.t13max.agent;

import com.t13max.agent.bean.Reload;
import com.t13max.agent.util.Log;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

/**
 * HotAgent
 *
 * @author: t13max
 * @since: 15:41 2024/8/12
 */
public class HotAgent {

    public HotAgent() {
    }

    public static void premain(String agentArgs, Instrumentation instrumentation) {
        Log.agent.info("HotAgent-premain-start");
        Log.agent.info("isRedefineClassesSupported : {}", instrumentation.isRedefineClassesSupported());
        regMBean(instrumentation);
    }

    public static void agentmain(String agentArgs, Instrumentation instrumentation) {
        premain(agentArgs, instrumentation);
    }

    private static void regMBean(Instrumentation instrumentation) {
        try {
            MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            //注册进目标程序的MBean服务器中 可以使用jmx调用
            mBeanServer.registerMBean(new Reload(instrumentation), new ObjectName("HotAgent:type=Reload"));
            Log.agent.info("HotAgent-rim-start");
        } catch (Throwable throwable) {
            Log.agent.error("HotAgent-rim-error", throwable);
        }
    }
}
