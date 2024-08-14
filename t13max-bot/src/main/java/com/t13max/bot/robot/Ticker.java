package com.t13max.bot.robot;

import com.t13max.bot.interfaces.IBot;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * ticker
 *
 * @author: t13max
 * @since: 14:32 2024/4/7
 */

@Log4j2
@Getter
@Setter
public class Ticker implements Runnable {

    private final int id;

    //这个Ticker内的机器人Map
    private final Map<Long, IBot> botMap = new ConcurrentHashMap<>();

    //停止标识
    private volatile boolean stop;

    private final int interval;

    private final int printInterval;

    private long lastPrintMills = System.currentTimeMillis();

    private long lastTickMills = System.currentTimeMillis();

    //运行线程池
    private final ExecutorService executorService;

    public Ticker(int id, ExecutorService executorService, int interval, int printInterval) {
        this.id = id;
        this.interval = interval;
        this.printInterval = printInterval;
        this.executorService = executorService;
    }

    public void addRoBot(IBot bot) {
        botMap.put(bot.getId(), bot);
    }

    public void tick() {
        //遍历所有机器人去tick
        botMap.values().forEach(IBot::tick);
        //打印监控信息
        printMonitorInfo();
    }

    /**
     * 打印监控信息
     *
     * @Author t13max
     * @Date 14:43 2024/4/7
     */
    private void printMonitorInfo() {
        log.info("tick: id: {} size: {}", id, botMap.size());
        botMap.values().forEach(item -> item.printMonitorInfo());
    }

    @Override
    public void run() {

        log.info("ticker 启动! tickerId={}", this.id);

        while (!stop) {

            log.debug("ticker tick! tickerId={}, botNum={}", this.id, botMap.size());

            long beginMills = System.currentTimeMillis();

            tick();

            long endMills = System.currentTimeMillis();

            this.lastTickMills = endMills;

            //运行时间
            long runMills = endMills - beginMills;

            try {
                long sleepMills = Math.max(0, interval - runMills);
                if (log.isDebugEnabled()) {
                    log.debug("sleep={}", sleepMills);
                }
                Thread.sleep(sleepMills);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        log.info("ticker stop! tickerId={}", this.id);
    }

    public IBot removeBot(long id) {
        IBot remove = botMap.remove(id);
        if (remove != null) {
            remove.close();
        }
        return remove;
    }

    /**
     * ticker启动!
     *
     * @Author t13max
     * @Date 16:42 2024/4/7
     */
    public void start() {
        this.executorService.execute(this);
    }

    /**
     * 停止一个ticker
     *
     * @Author t13max
     * @Date 14:45 2024/4/7
     */
    public void shutdown() {
        //标记置为停止
        this.stop = true;
        //先关线程池
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                    log.error("线程池关不掉!");
                }
            }
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }
        //再关机器人
        botMap.values().forEach(IBot::close);
    }

}

