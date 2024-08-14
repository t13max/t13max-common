package com.t13max.bot.manager;

import com.t13max.bot.entity.RobotConfig;
import com.t13max.bot.interfaces.IBot;
import com.t13max.bot.robot.RobotAccount;
import com.t13max.bot.robot.RobotFactory;
import com.t13max.bot.robot.Ticker;
import com.t13max.util.RandomUtil;
import lombok.extern.log4j.Log4j2;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 机器人管理类
 *
 * @author: t13max
 * @since: 13:54 2024/4/7
 */

@Log4j2
public class RobotManager {
    //机器人集合 支持动态增减
    private static final Map<Long, IBot> botMap = new ConcurrentHashMap<>();
    //ticker集合 不会动态增减
    private static final Map<Integer, Ticker> tickerMap = new HashMap<>();
    //配置默认名字
    private static final String DEFAULT_YAML = "robot-config.yaml";
    //机器人账号信息
    private static final String ROBOT_INFO = "robot-info.txt";
    //如果一次添加大量机器人 分批添加 一次100个
    private static final int BATCH_NUM = 100;
    //分批间隔
    private static final int BATCH_INTERVAL = 1 * 1000;
    private static final String ACCOUNT_FILE = "ACCOUNT_FILE";

    private boolean init = false;
    //机器人id
    private final AtomicInteger botId = new AtomicInteger(10001);
    //账号信息
    private final LinkedList<RobotAccount> robotAccountList = new LinkedList<>();
    //新增账号信息
    private final LinkedList<RobotAccount> newRobotAccountList = new LinkedList<>();
    //配置
    private final RobotConfig robotConfig;
    //机器人工厂
    private RobotFactory robotFactory;
    //监控其他ticker
    private ScheduledExecutorService watcherExecutor;

    public RobotManager() {
        this(null);
    }

    public RobotManager(String configFile) {

        Yaml yaml = new Yaml();

        if (configFile == null || configFile.isEmpty()) {
            configFile = DEFAULT_YAML;
        }

        RobotConfig robotConfig = yaml.loadAs(RobotManager.class.getClassLoader().getResourceAsStream(configFile), RobotConfig.class);
        if (robotConfig == null) {
            robotConfig = RobotConfig.getDefaultConfig();
        } else {
            //校验正确性
            robotConfig.check();
        }

        this.robotConfig = robotConfig;

        this.addShutDownHook();
    }

    /**
     * 启动, 初始化线程池ticker啥的
     *
     * @Author t13max
     * @Date 17:35 2024/4/7
     */
    public void start() {

        log.info("压测, 启动!");

        if (init) return;

        init = true;

        //创建工厂
        this.robotFactory = new RobotFactory();

        //初始化ticker并启动
        initTickerAndRun();

        //初始化watcher 调试阶段注掉
        //initWatcherAndRun();

        //加载账号信息
        loadAccountList();

        //根据配置添加机器人
        initRobot();

        log.info("启动完成!");
    }

    /**
     * 初始化watcher
     *
     * @Author t13max
     * @Date 18:06 2024/4/7
     */
    private void initWatcherAndRun() {
        watcherExecutor = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "ticker-watcher");
            }
        });

        //以超时时间为间隔去检查 不用太精确 主要是希望如果卡死了能感知到
        watcherExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                long nowMills = System.currentTimeMillis();
                log.info("watcherExecutor 执行!");
                for (Ticker ticker : tickerMap.values()) {
                    if (nowMills - ticker.getLastTickMills() > robotConfig.getTickerTimeout()) {
                        log.error("ticker{}卡死了, ", ticker.getId());
                        ticker.shutdown();
                        //后续处理
                    }
                }
                log.info("watcherExecutor 执行完毕!");
            }
        }, robotConfig.getTickerTimeout(), robotConfig.getTickerTimeout(), TimeUnit.MILLISECONDS);
    }

    /**
     * 初始化ticker
     *
     * @Author t13max
     * @Date 18:06 2024/4/7
     */
    private void initTickerAndRun() {
        int num = robotConfig.getTickerNum();

        //运行线程池
        for (int i = 0; i < num; i++) {
            int id = i;
            ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r, "ticker-run-" + id);
                }
            });
            tickerMap.put(id, new Ticker(i, executorService, this.robotConfig.getTickerInterval(), this.robotConfig.getTickerPrintInterval()));
        }

        //遍历启动
        for (Ticker ticker : tickerMap.values()) {
            ticker.start();
        }
    }

    /**
     * 加载账号列表
     *
     * @Author t13max
     * @Date 18:06 2024/4/7
     */
    private void loadAccountList() {

        String local = System.getenv(ACCOUNT_FILE);
        if (local == null || local.isEmpty()) {
            local = "/Users/antingbi/idea/bot/robot-info.txt";
        }
        File file = new File(local);
        try {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    log.error("账号文件文件创建失败");
                    return;
                }
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            String readLine;

            while ((readLine = bufferedReader.readLine()) != null) {
                if (readLine.startsWith("#") || readLine.isEmpty()) continue;
                RobotAccount robotAccount = RobotAccount.parse(readLine);
                if (robotAccount == null) {
                    log.error("机器人账号解析失败, readLine={}", readLine);
                    return;
                }
                robotAccountList.add(robotAccount);
                if (robotAccount.getId() > botId.get()) {
                    botId.set(robotAccount.getId());
                }
            }
            bufferedReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据配置初始化机器人
     *
     * @Author t13max
     * @Date 17:35 2024/4/7
     */
    private void initRobot() {

        for (RobotConfig.BotGroupConfig botGroupConfig : this.robotConfig.getBotList()) {
            int num = botGroupConfig.getNum();
            int serverId = botGroupConfig.getServerId();
            String url = botGroupConfig.getUrl();
            this.batchAddRoBot(url, serverId, num);
        }
    }

    /**
     * 批量添加机器人
     *
     * @Author t13max
     * @Date 18:06 2024/4/7
     */
    public void batchAddRoBot(String url, int serverId, int num) {

        while (num > 0) {
            int min = Math.min(BATCH_NUM, num);
            for (int i = 0; i < min; i++) {
                doAddRoBot(url, serverId);
            }
            num -= min;
            try {
                Thread.sleep(BATCH_INTERVAL);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        //保存新建的账号
        saveAccountInfo();
    }

    /**
     * 单个添加
     *
     * @Author t13max
     * @Date 18:44 2024/4/7
     */
    public void addRoBot(String gatewayUri, int serverId) {

        doAddRoBot(gatewayUri, serverId);

        //保存新建的账号
        saveAccountInfo();
    }

    /**
     * 保存账号信息
     *
     * @Author t13max
     * @Date 18:49 2024/4/7
     */
    private void saveAccountInfo() {

        File file;

        try {

            String local = System.getenv(ACCOUNT_FILE);
            if (local == null || local.isEmpty()) {
                local = "/Users/antingbi/idea/bot/robot-info.txt";
            }
            file = new File(local);

            if (!file.exists()) {
                boolean newFile = file.createNewFile();
                if (!newFile) {
                    log.error("账号信息保存失败, 无法创建文件");
                    return;
                }
            }
            if (file.isDirectory()) {
                log.error("账号信息保存失败, 路径错误");
                return;
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
            BufferedWriter bw = new BufferedWriter(fw);
            for (RobotAccount robotAccount : this.newRobotAccountList) {
                bw.write(robotAccount.toString());
                bw.newLine();
            }
            bw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.newRobotAccountList.clear();
    }

    /**
     * 添加机器人
     *
     * @Author t13max
     * @Date 18:39 2024/4/7
     */
    private void doAddRoBot(String url, int serverId) {
        RobotAccount robotAccount;
        String password = robotConfig.getPassword();
        if (password == null || password.isEmpty()) {
            password = randomPassword();
        }
        if (this.robotAccountList.isEmpty()) {
            robotAccount = new RobotAccount(botId.incrementAndGet(), robotConfig.getPrefix(), password,  serverId);
            newRobotAccountList.add(robotAccount);
        } else {
            robotAccount = this.robotAccountList.removeFirst();
            if (robotAccount == null) {
                robotAccount = new RobotAccount(botId.incrementAndGet(), robotConfig.getPrefix(), password, serverId);
                newRobotAccountList.add(robotAccount);
            } else {
                robotAccount.setServerId(serverId);
            }
        }
        IBot bot = this.robotFactory.createPressureBot(url, serverId, robotAccount);
        this.doAddRoBot(bot);
    }

    public String randomPassword() {
        StringBuilder result = new StringBuilder();
        while (result.length() < 10) {
            int ascii = RandomUtil.nextInt(122 - 48) + 48;
            //忽略字符
            if (ascii <= 64 && ascii >= 48) continue;
            if (ascii <= 96 && ascii >= 91) continue;
            result.append((char) ascii);
        }
        return result.toString();
    }

    private void doAddRoBot(IBot bot) {
        Ticker ticker = getTicker(bot.getId());
        ticker.addRoBot(bot);
        botMap.put(bot.getId(), bot);
        log.info("addRobot, ticker={} botId={}", ticker.getId(), bot.getId());
    }

    public IBot getRoBotById(long id) {
        return botMap.get(id);
    }

    public Ticker getTicker(long id) {
        int tickerNum = robotConfig.getTickerNum();
        return tickerMap.get(tickerNum == 1 ? 0 : (int) id % tickerNum);
    }

    public void removeBot(long id) {
        Ticker ticker = getTicker(id);
        ticker.removeBot(id);
        botMap.remove(id);
    }

    public void destroy() {

        log.info("压测, 关闭!");

        for (Ticker ticker : tickerMap.values()) {
            ticker.shutdown();
        }

        log.info("压测, 关闭完成!");
    }

    public void removeAllBot() {
        botMap.keySet().forEach(this::removeBot);
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroy));
    }
}
