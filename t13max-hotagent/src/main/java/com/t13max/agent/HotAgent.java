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

    public static void premain(String var0, Instrumentation instrumentation) {
        Log.agent.info("HotAgent-premain-start");
        Log.agent.info("isRedefineClassesSupported : {}", instrumentation.isRedefineClassesSupported());
        regMBean(instrumentation);
    }

    public static void agentmain(String var0, Instrumentation var1) {
        premain(var0, var1);
    }

    private static void regMBean( Instrumentation instrumentation) {
        try {
            MBeanServer var1 = ManagementFactory.getPlatformMBeanServer();
            var1.registerMBean(new Reload(instrumentation), new ObjectName("HotAgent:type=Reload"));
            Log.agent.info("HotAgent-rim-start");
        } catch (Throwable var2) {
            Log.agent.error("HotAgent-rim-error", var2);
        }

    }
}
