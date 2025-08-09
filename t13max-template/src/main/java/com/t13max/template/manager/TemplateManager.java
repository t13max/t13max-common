package com.t13max.template.manager;


import com.t13max.common.exception.CommonException;
import com.t13max.common.manager.ManagerBase;
import com.t13max.common.util.Log;
import com.t13max.template.exception.TemplateException;
import com.t13max.template.helper.TemplateHelper;
import com.t13max.util.PackageUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * reload成功后替换 否则撤销操作 后续优化
 *
 * @author: t13max
 * @since: 14:02 2024/4/11
 */
public class TemplateManager extends ManagerBase {

    private final Map<String, TemplateHelper<?>> helperMap = new HashMap<>();

    /**
     * 获取当前实例对象
     *
     * @Author t13max
     * @Date 16:44 2024/5/23
     */
    public static TemplateManager inst() {
        return ManagerBase.inst(TemplateManager.class);
    }

    @Override
    public void init() {

        try {
            Set<Class<?>> classSet = PackageUtil.scan("com.t13max.template.helper");
            //创建实例
            for (Class<?> clazz : classSet) {
                // 只需要加载TemplateHelper注解数据
                if (!TemplateHelper.class.isAssignableFrom(clazz) || Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }

                // 创建实例
                Object inst = clazz.getDeclaredConstructor().newInstance();
                TemplateHelper<?> helper = (TemplateHelper<?>) inst;
                helperMap.put(clazz.getName(), helper);
            }

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new CommonException(e);
        }

        if (!this.load()) {
            throw new TemplateException("配表加载失败");
        }

    }

    /**
     * 第一次加载表
     *
     * @Author t13max
     * @Date 14:36 2024/5/23
     */
    public boolean load() {
        try {
            helperMap.values().forEach(TemplateHelper::load);

            for (TemplateHelper<?> templateHelper : helperMap.values()) {
                if (!templateHelper.configCheck()) {
                    //直接抛出异常 不让起服
                    throw new CommonException("加载表失败");
                }
            }

            helperMap.values().forEach(TemplateHelper::transfer);

        } catch (Exception e) {
            throw e;
        }
        return true;
    }

    /**
     * 重新加载表 指定表
     *
     * @Author t13max
     * @Date 14:36 2024/5/23
     */
    public boolean reload(List<String> nameList) {
        for (String name : nameList) {
            if (!reload(name)) {
                return false;
            }
        }
        for (String name : nameList) {
            TemplateHelper<?> templateHelper = helperMap.get(name);
            if (!templateHelper.configCheck()) {
                //直接抛出异常 不让起服
                return false;
            }
        }
        for (String name : nameList) {
            TemplateHelper<?> templateHelper = helperMap.get(name);
            templateHelper.transfer();
        }
        return true;
    }

    public boolean reload(String name) {
        TemplateHelper<?> templateHelper = helperMap.get(name);
        if (templateHelper == null) {
            Log.TEMPLATE.error("reload失败, templateHelper为空, name={}", name);
            return false;
        }
        try {
            templateHelper.reload();
        } catch (Exception e) {
            Log.TEMPLATE.error("reload失败, name={}",name, e);
            return false;
        }
        return true;
    }

    /**
     * 重新加载表 全部
     *
     * @Author t13max
     * @Date 16:42 2024/5/23
     */
    public boolean reload() {
        return reloadWithPath(null);
    }

    public boolean reloadWithPath(String path) {
        try {
            helperMap.values().forEach(e -> e.reload(path));
            for (TemplateHelper<?> templateHelper : helperMap.values()) {
                if (!templateHelper.configCheck()) {
                    return false;
                }
            }
            helperMap.values().forEach(TemplateHelper::transfer);
        } catch (Exception e) {
            Log.TEMPLATE.error("reloadWithPath失败, path={}}", path,e);
            return false;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public <T extends TemplateHelper<?>> T helper(Class<T> clazz) {
        return (T) this.helperMap.get(clazz.getName());
    }

}
