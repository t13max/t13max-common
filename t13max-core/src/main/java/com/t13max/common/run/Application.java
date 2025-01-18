package com.t13max.common.run;

import com.t13max.common.config.BaseConfig;
import com.t13max.common.manager.ManagerBase;
import com.t13max.common.net.AbstractServer;
import com.t13max.common.util.Log;
import com.t13max.util.func.Applier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.concurrent.locks.LockSupport;

/**
 * 一个t13max 应用
 * 为了统一 规范化 启动流程
 *
 * @author: t13max
 * @since: 17:29 2024/5/23
 */
public class Application {

    //配置
    private static BaseConfig config;
    //实例名
    private static String instanceName;
    //运行状态
    private static volatile boolean running;

    /**
     * Application启动
     *
     * @Author t13max
     * @Date 18:10 2024/8/23
     */
    public static synchronized void run(Class<?> clazz, String[] args) throws Exception {

        try {

            //防止重复启动
            if (running) return;
            //日志级别
            checkLogLevel();
            //加载配置
            config = BaseConfig.loadConfig(clazz);
            //初始化所有manager
            ManagerBase.initAllManagers();
            //实例名
            instanceName = config.getInstanceName();
            //启动Netty服务器
            AbstractServer.initServer(clazz);
            //添加停服钩子 manager shutdown
            addShutdownHook(ManagerBase::shutdown);
            //启动完成
            running = true;
        } catch (Exception e) {
            //遇到任何异常 直接退出
            e.printStackTrace();
            System.exit(0);
        }

    }

    /**
     * 极简启动 只初始化配置 其他全部交由外部处理
     *
     * @Author t13max
     * @Date 18:13 2024/8/23
     */
    public static synchronized void run(Class<?> clazz, String[] args, Applier applier) throws Exception {

        try {

            //防止重复启动
            if (running) return;
            //日志级别
            checkLogLevel();
            //加载配置
            config = BaseConfig.loadConfig(clazz);
            //执行外部逻辑
            applier.apply();
            //启动完成
            running = true;
        } catch (Exception e) {
            //遇到任何异常 直接退出
            e.printStackTrace();
            System.exit(0);
        }

    }

    public static synchronized void run(String[] args, Applier applier) throws Exception {

        try {

            //防止重复启动
            if (running) return;
            //日志级别
            checkLogLevel();
            //执行外部逻辑
            applier.apply();
            //启动完成
            running = true;
        } catch (Exception e) {
            //遇到任何异常 直接退出
            e.printStackTrace();
            System.exit(0);
        }

    }

    private static void checkLogLevel() {
        String logLevel = System.getenv("LOG_LEVEL");
        if (logLevel == null) {
            Log.APP.info("日志级别默认!");
            return;
        }
        // 获取 LoggerContext
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        String loggerName = "com.t13max";
        // 根据级别设置日志等级
        switch (logLevel.toUpperCase()) {
            case "DEBUG":
                Configurator.setLevel(loggerName, org.apache.logging.log4j.Level.DEBUG);
                break;
            case "INFO":
                Configurator.setLevel(loggerName, org.apache.logging.log4j.Level.INFO);
                break;
            case "WARN":
                Configurator.setLevel(loggerName, org.apache.logging.log4j.Level.WARN);
                break;
            case "ERROR":
                Configurator.setLevel(loggerName, org.apache.logging.log4j.Level.ERROR);
                break;
            default:
                Log.APP.error("日志级别配置错误! level={}", logLevel);
        }

        // 打印当前日志级别
        Log.APP.error("当前日志级别 level={}", logLevel);
    }

    /**
     * 获取配置文件
     *
     * @Author t13max
     * @Date 17:58 2024/5/23
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseConfig> T config() {
        return (T) config;
    }

    /**
     * 添加停服钩子
     *
     * @Author t13max
     * @Date 18:32 2024/5/23
     */
    public static void addShutdownHook(Runnable runnable) {
        Runtime.getRuntime().addShutdownHook(new Thread(runnable));
    }

}
