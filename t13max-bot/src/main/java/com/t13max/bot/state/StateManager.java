package com.t13max.bot.state;

import com.t13max.common.exception.CommonException;
import com.t13max.common.manager.ManagerBase;
import com.t13max.util.PackageUtil;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author: t13max
 * @since: 17:37 2024/8/14
 */
public class StateManager extends ManagerBase {

    public static final String LOGIN_STATE = "LoginState";

    private final Map<String, Supplier<IBotState>> stateMap = new HashMap<>();

    public static StateManager inst() {
        return ManagerBase.inst(StateManager.class);
    }

    @Override
    public void init() {

        try {
            Set<Class<?>> classSet = PackageUtil.scan("com.t13max");
            //创建实例
            for (Class<?> clazz : classSet) {
                // 只需要加载TemplateHelper注解数据
                if (!IBotState.class.isAssignableFrom(clazz) || Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }
                //是初始状态
                EnterState annotation = clazz.getAnnotation(EnterState.class);
                if (annotation == null) continue;
                //获取构造器
                Supplier<IBotState> supplier = () -> {
                    try {
                        return (IBotState) clazz.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
                this.stateMap.put(clazz.getSimpleName(), supplier);
            }

        } catch (Exception e) {
            throw new CommonException(e);
        }

    }

    public IBotState getState(String name) {
        Supplier<IBotState> supplier = stateMap.get(name);
        if (supplier == null) {
            return null;
        }
        return supplier.get();
    }
}
