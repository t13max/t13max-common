package com.t13max.bot.entity;

import com.t13max.bot.exception.RobotException;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 机器人配置类
 *
 * @author: t13max
 * @since: 15:41 2024/4/7
 */
@Data
public class RobotConfig {
    //ticker相关参数
    private Ticker ticker;
    //机器人组配置
    private List<BotGroupConfig> botList;
    //账号前缀
    private String prefix;
    //账号密码
    private String password = "123456";

    public RobotConfig() {
    }

    /**
     * 获取默认配置
     *
     * @Author t13max
     * @Date 16:12 2024/4/7
     */
    public static RobotConfig getDefaultConfig() {
        RobotConfig robotConfig = new RobotConfig();
        robotConfig.setTicker(Ticker.getDefaultTicker());
        robotConfig.setBotList(Collections.singletonList(BotGroupConfig.getDefaultBotConfig()));
        return robotConfig;
    }

    /**
     * 校验配置正确性
     *
     * @Author t13max
     * @Date 16:29 2024/4/7
     */
    public void check() {
        if (this.ticker == null) {
            throw new RobotException("ticker为空");
        }
        if (this.getTickerNum() <= 0) {
            throw new RobotException("tickerNum小于等于0, num=" + this.getTickerNum());
        }

        if (this.getTickerInterval() <= 0) {
            throw new RobotException("tickerInterval小于等于0, internal=" + this.getTickerInterval());
        }

        List<BotGroupConfig> botList = this.getBotList();
        if (botList == null) {
            throw new RobotException("机器人信息为空");
        }
        for (BotGroupConfig botGroupConfig : botList) {
            if (botGroupConfig.getNum() <= 0) {
                throw new RobotException("机器人数量小于等于0, num=" + botGroupConfig.getNum());
            }
            if (botGroupConfig.getServerId() <= 0) {
                throw new RobotException("机器人服务器id小于等于0, serverId=" + botGroupConfig.getServerId());
            }
            if (botGroupConfig.getUrl() == null || botGroupConfig.getUrl().isEmpty()) {
                throw new RobotException("机器人gatewayUri配置错误, msg=" + botGroupConfig.getUrl());
            }
        }
    }

    /**
     * ticker配置
     *
     * @Author t13max
     * @Date 16:05 2024/4/7
     */
    @Data
    public static class Ticker {
        //ticker数量
        private int num;
        //tick间隔
        private int interval;
        //打印间隔
        private int printInterval;
        //监控超时时间
        private int timeout;

        /**
         * 默认配置
         *
         * @Author t13max
         * @Date 16:11 2024/4/7
         */
        private static Ticker getDefaultTicker() {
            Ticker ticker = new Ticker();
            ticker.setNum(1);//ticker.setNum(Runtime.getRuntime().availableProcessors());
            ticker.setInterval(200);
            ticker.setPrintInterval(10 * 1000);
            ticker.setTimeout(60 * 1000);
            return ticker;
        }

    }

    /**
     * 机器人信息配置
     *
     * @Author t13max
     * @Date 17:26 2024/4/7
     */
    @Data
    public static class BotGroupConfig {
        //地址
        private String url;
        //服务器id
        private int serverId;
        //机器人数量
        private int num;
        //目标 0为随机测试没目标
        private int target;
        //目标相关参数
        private String targetParam;

        /**
         * 默认配置
         *
         * @Author t13max
         * @Date 16:11 2024/4/7
         */
        private static BotGroupConfig getDefaultBotConfig() {
            BotGroupConfig botGroupConfig = new BotGroupConfig();
            botGroupConfig.setNum(1);
            botGroupConfig.setServerId(10001);
            botGroupConfig.setUrl("http://192.168.xx.xx:xxxx");
            return botGroupConfig;
        }
    }

    public int getTickerNum() {
        return this.getTicker().getNum();
    }

    public int getTickerInterval() {
        return this.getTicker().getInterval();
    }

    public int getTickerPrintInterval() {
        return this.getTicker().getPrintInterval();
    }

    public int getTickerTimeout() {
        return this.getTicker().getTimeout();
    }

}
