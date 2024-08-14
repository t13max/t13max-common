package com.t13max.bot.business;


import com.t13max.ai.behavior4j.BehaviorTree;
import com.t13max.ai.behavior4j.utils.BehaviorTreeManager;
import com.t13max.ai.fsm.DefaultStateMachine;
import com.t13max.bot.robot.SmartRobot;
import com.t13max.bot.state.EmptyState;

/**
 * 智能业务
 *
 * @author: t13max
 * @since: 16:07 2024/8/8
 */
public class SmartBusiness extends AbstractBusiness {

    private final BehaviorTree<SmartRobot> behaviorTree;

    public SmartBusiness() {
        this.stateMachine = new DefaultStateMachine<>(bot, EmptyState.EMPTY_STATE);
        this.behaviorTree = BehaviorTreeManager.getInstance().createBehaviorTree("BotTree");
    }

    /**
     * 智能业务的doTick是走行为树
     * 具体如何tick 切换状态 由行为树决定
     *
     * @Author t13max
     * @Date 16:08 2024/8/9
     */
    @Override
    protected void doTick() {
        //刷新行为树 智能决定当前要做什么
        behaviorTree.update();
    }

    @Override
    public void exit() {
        onExit();
        changeStatus(EmptyState.EMPTY_STATE);
    }

    @Override
    protected int getTickCountLimit() {
        return Integer.MAX_VALUE;
    }

    @Override
    protected int getBusinessCountLimit() {
        return Integer.MAX_VALUE;
    }

    //选择小业务
    @Override
    public void choiceSmallBusiness() {
        //智能机器人不在这里选择业务 完全根据行为树决定做什么
    }
}
