package com.t13max.bot.business;

import com.google.protobuf.AbstractMessage;
import com.t13max.ai.fsm.IState;
import com.t13max.ai.fsm.StateMachine;
import com.t13max.bot.consts.IStatusEnum;
import com.t13max.bot.interfaces.IBot;
import com.t13max.bot.interfaces.IBusiness;
import com.t13max.bot.state.IBotState;
import com.t13max.util.RandomUtil;
import lombok.extern.log4j.Log4j2;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务抽象类
 *
 * @author: t13max
 * @since: 13:16 2024/4/19
 */
@Log4j2
public abstract class AbstractBusiness implements IBusiness {

    //最大tick次数
    public final static int MAX_TICK = 1000;
    //测试的业务个数
    public final static int MAX_BUSINESS_COUNT = 3;

    //当前业务对象绑定的机器人
    protected IBot bot;
    //总tick次数
    protected int tickCount;
    //小业务执行次数
    protected byte testCount;
    //业务状态
    protected volatile IStatusEnum businessStatus;
    //进入当前状态的时间戳
    protected long curStatusStartMills = System.currentTimeMillis();
    //24.8.6新版状态机
    protected StateMachine<IBot, IBotState> stateMachine;
    //tick等待次数
    private final Map<IBotState, Integer> waitTickMap = new ConcurrentHashMap<>();
    @Deprecated
    private final Map<IStatusEnum, Integer> waitTick = new ConcurrentHashMap<>();

    @Override
    public void init(IBot bot) {
        if (this.bot != null) return;
        this.bot = bot;
    }

    @Override
    public void tick() {

        //tick超过一定次数(防卡死) 或 小业务执行过一定次数 退出吧
        if (tickCount > getTickCountLimit() || testCount >= getBusinessCountLimit()) {
            this.exit();
            return;
        }

        log.debug("Business tick, tickCount={}, status={}", this.tickCount++, this.businessStatus);

        try {
            doTick();
        } catch (Exception exception) {
            //tick的异常处理
            exception.printStackTrace();
        }

    }

    @Override
    public boolean checkTimeout(IBotState state, int count) {
        Integer waitCount = waitTickMap.getOrDefault(state, 0);
        if (waitCount < count) {
            waitTickMap.put(state, waitCount + 1);
            return false;
        }
        waitTickMap.remove(state);
        return true;
    }

    /**
     * @param statusEnum
     * @param waitNum    等待的次数
     * @return 是否等待到了waitNum
     */
    @Deprecated
    public boolean waitTick(IStatusEnum statusEnum, int waitNum) {
        var i = waitTick.getOrDefault(statusEnum, 0);
        if (i > waitNum) {
            waitTick.put(statusEnum, 0);
            return false;
        }
        waitTick.put(statusEnum, i + 1);
        return true;
    }


    /**
     * doTick方法
     * 不实现默认走新状态机
     * 老的都重写了 不影响老的 后面慢慢调整
     *
     * @Author t13max
     * @Date 16:38 2024/8/8
     */
    protected void doTick() {
        this.stateMachine.update();
    }

    protected void onExit() {
        this.tickCount = 0;
        this.testCount = 0;
        log.debug("{} exit! botId={}", this.getClass().getSimpleName(), bot.getId());
    }

    //更新模块状态
    @Override
    public void changeStatus(IStatusEnum statusEnum) {
        this.businessStatus = statusEnum;
        changeStatus(statusEnum.newState());
    }

    @Override
    public void changeStatus(IBotState state) {
        this.stateMachine.changeState(state);
        this.curStatusStartMills = System.currentTimeMillis();
    }

    //主动退出
    @Override
    public void exit() {
        onExit();
        bot.choiceBusiness();
    }

    //选择小业务
    @Override
    public void choiceSmallBusiness() {
        //随机测试业务
        changeStatus(RandomUtil.random(businessStatus.getValues(), IStatusEnum::getWeight));
        testCount++;
    }

    /**
     * 检测超时
     * 入参为多少时间算作超时
     * 以进入当前状态的时间戳作为基准
     *
     * @Author t13max
     * @Date 15:51 2024/5/14
     */
    protected boolean checkTimeout(long timeoutMills) {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - curStatusStartMills > timeoutMills) {
            return true;
        }
        return false;
    }

    /**
     * 获取最大tick次数 用于防卡死
     * 有些玩法需要很多次tick则重写此方法
     * 注意自己做超时之类的判断 防止卡死
     *
     * @Author t13max
     * @Date 17:11 2024/5/14
     */
    protected int getTickCountLimit() {
        return MAX_TICK;
    }

    protected int getBusinessCountLimit() {
        return MAX_BUSINESS_COUNT;
    }

    /**
     * 打印当前业务的信息
     */
    public void printMonitorInfo() {
        log.info("businessStatus: {}", businessStatus);
    }

    @Override
    public StateMachine<IBot, IBotState> getStateMachine() {
        return this.stateMachine;
    }

    @Override
    public IBotState getCurState() {
        return this.stateMachine.getCurrentState();
    }
}
