package com.t13max.common.manager;

import com.t13max.common.dag.DAG;
import com.t13max.common.dag.DAGNode;
import com.t13max.common.exception.CommonException;
import com.t13max.common.util.Log;
import com.t13max.util.PackageUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * manger基类
 *
 * @author: t13max
 * @since: 14:09 2024/5/23
 */
public abstract class ManagerBase implements Comparable<ManagerBase> {

    //缓存所有manager
    private final static Map<String, ManagerBase> INSTANCES = new HashMap<>();

    // manager依赖图
    private final static DAG<ManagerBase> DAG = new DAG<>();

    //初始化标记
    private final static AtomicBoolean INIT = new AtomicBoolean();

    //顺序 越小越先执行
    protected int order;

    static {
        initialize();
    }

    @SuppressWarnings("unchecked")
    protected static <T extends ManagerBase> T inst(Class<T> clazz) {
        Object inst = INSTANCES.get(clazz.getName());
        if (inst == null) {
            throw new CommonException("获取Manager实例时出错：未能找到对应实例，class=" + clazz.getSimpleName());
        }

        return (T) inst;
    }

    /**
     * 自动扫描com.t13max包下的manager并初始化
     *
     * @Author: t13max
     * @Since: 22:34 2024/7/19
     */
    private static void initialize() {

        INSTANCES.clear();

        Set<Class<?>> classSet = PackageUtil.scan("com.t13max");

        //创建实例
        initialize(classSet);
    }

    /**
     * 初始化指定类的manager
     * 不是manager自动跳过
     *
     * @Author: t13max
     * @Since: 22:35 2024/7/19
     */
    private static void initialize(Set<Class<?>> classSet) {

        try {

            for (Class<?> clazz : classSet) {
                // 只需要加载ManagerClass注解数据
                if (!ManagerBase.class.isAssignableFrom(clazz) || Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }

                // 创建实例
                Object inst = clazz.getDeclaredConstructor().newInstance();
                ManagerBase managerBase = (ManagerBase) inst;
                INSTANCES.put(clazz.getName(), managerBase);
            }

            //创建DAGNode
            for (ManagerBase managerBase : INSTANCES.values()) {
                final DAGNode<ManagerBase> dg = DAG.createNodeIfNotExist(managerBase);
                if (!managerBase.getDependents().isEmpty()) {
                    for (final Class<? extends ManagerBase> key : managerBase.getDependents()) {
                        dg.addPrev(INSTANCES.get(key.getName()));
                    }
                }
            }
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new CommonException(e);
        }
    }

    /**
     * 初始化所有的manager
     */
    static void initAllManagers() {

        if (!INIT.compareAndSet(false, true)) {
            Log.MANAGER.error("无法initAllManagers, 已经INIT!");
            return;
        }
        DAG.walk(n -> {
            ManagerBase managerBase = n.getObj();
            try {
                // 初始化
                managerBase.baseInit();
                Log.MANAGER.debug("模块{}初始化完成", managerBase.getClass().getName());
            } catch (final Exception ex) {
                Log.MANAGER.error("模块{}启动失败", managerBase.getClass().getName(), ex);
                throw new CommonException(ex);
            }
        });
    }

    /**
     * 关服
     */
    public static void shutdown() {
        DAG.walk(n -> {
            ManagerBase manager = n.getObj();
            try {
                manager.onShutdown();
            } catch (Exception e) {
                Log.MANAGER.info("Manager: {} shutdown failed, error: {}", manager.getClass().getCanonicalName(), e);
            }
        });
    }

    /**
     * 关服时的处理
     */
    protected void onShutdown() {

    }

    /**
     * 返回当前Manager依赖的其他Manger类名
     */
    public List<Class<? extends ManagerBase>> getDependents() {
        return Collections.emptyList();
    }

    /**
     * 基类的初始化
     *
     * @Author: t13max
     * @Since: 17:17 2025/8/9
     */
    private void baseInit() {
        if (INIT.get()) {
            Log.MANAGER.error("无法init, 已经INIT!");
            return;
        }
        this.init();
    }

    protected void init() {

    }

    @Override
    public int compareTo(ManagerBase o) {
        //都小于0 根据名字字典排
        if (this.order < 0 && o.order < 0) {
            return this.getClass().getName().compareTo(o.getClass().getName());
        }
        //负号排后面
        if (this.order < 0) return 1;
        if (o.order < 0) return -1;
        //数字越小越靠前
        return Integer.compare(this.order, o.order);
    }


    /**
     * 设置顺序
     *
     * @Author: t13max
     * @Since: 17:18 2025/8/9
     */
    public void setOrder(int order) {
        if (order != -1) {
            return;
        }
        this.order = order;
    }
}
