package com.t13max.common.action;

import com.t13max.common.config.ActionConfig;
import com.t13max.common.run.Application;
import com.t13max.common.util.Log;
import com.t13max.util.ThreadNameFactory;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ActionExecutor {

    private int corePoolSize;

    private int maxPoolSize;

    private String name;

    private ThreadPoolExecutor executor;

    private boolean isRunning = true;

    public static ActionExecutor createExecutor() {
        ActionConfig action = Application.config().getAction();
        ActionExecutor executor = new ActionExecutor();
        executor.corePoolSize = action.getCore();
        executor.maxPoolSize = action.getMax();
        executor.name = action.getName();
        executor.init();
        return executor;
    }

    private void init() {
        this.name = name == null ? "action-executor" : name;
        //超出corePoolSize数量之后的线程 超过5分钟空闲将被回收
        int keepAliveTime = 5;
        TimeUnit unit = TimeUnit.MINUTES;

        final LinkedTransferQueue<Runnable> workQueue = new LinkedTransferQueue<Runnable>();
        final RejectedExecutionHandler handler = (r, e) -> {
        };

        executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, new ThreadNameFactory(this.name), handler);

        Log.ACTION.info("corePoolSize:{} maxPoolSize:{}", corePoolSize, maxPoolSize);
    }

    public int queueSize() {
        return executor.getQueue().size();
    }

    public void execute(Runnable action) {
        executor.execute(action);
    }

    public synchronized void shutdown() {
        if (isRunning) {
            if (!executor.isShutdown()) {
                executor.shutdown();
            }
            isRunning = false;
        }
    }
}
