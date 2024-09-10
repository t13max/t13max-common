package com.t13max.common.ioc;


import com.t13max.common.exception.CommonException;
import com.t13max.common.ioc.annotaion.Autowired;
import com.t13max.common.ioc.annotaion.Component;
import com.t13max.common.manager.ManagerBase;
import com.t13max.util.PackageUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 极简IOC容器
 *
 * @author: t13max
 * @since: 14:33 2024/6/6
 */
public class IocManager extends ManagerBase {

    private static final Map<String, Object> objectMap = new HashMap<>();
    private static final Map<String, Object> preObjectMap = new HashMap<>();

    @Override
    protected void init() {
        try {
            initBean();
        } catch (Exception e) {
            throw new CommonException(e);
        }
    }

    @Override
    protected void onShutdown() {
        super.onShutdown();
    }

    public <T> T getBean(Class<T> clazz) {
        return (T)objectMap.get(clazz.getName());
    }

    public <T> T getBean(String name) {
        return (T)objectMap.get(name);
    }

    private static void initBean() throws Exception {
        Set<Class<?>> classes = PackageUtil.scan("com.t13max");
        List<Class<?>> createList = new LinkedList<>();
        for (Class<?> clazz : classes) {
            Component component = clazz.getAnnotation(Component.class);
            if (component == null) {
                continue;
            }
            createList.add(clazz);
        }
        createBean(createList);
    }

    private static void createBean(List<Class<?>> createList) {
        for (Class<?> clazz : createList) {
            if (objectMap.containsKey(clazz.getName())) {
                continue;
            }
            createBean(clazz);
        }
    }

    private static Object createBean(Class<?> clazz) {
        Object object = null;
        try {
            Constructor<?> constructor = clazz.getConstructor();
            object = constructor.newInstance();
            preObjectMap.put(clazz.getName(), object);
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                declaredField.setAccessible(true);
                Autowired autoWired = declaredField.getAnnotation(Autowired.class);
                if (autoWired == null) {
                    continue;
                }
                Class<?> fieldClazz = declaredField.getType();
                Component component = fieldClazz.getAnnotation(Component.class);
                if (component == null) {
                    throw new CommonException("自动注入的字段找不到对象" + fieldClazz.getName() + "::" + fieldClazz.getName());
                }
                Object fieldObject = objectMap.get(fieldClazz.getName());
                if (fieldObject == null) {
                    fieldObject = preObjectMap.get(fieldClazz.getName());
                }
                if (fieldObject == null) {
                    fieldObject = createBean(fieldClazz);
                }
                if (fieldObject == null) {
                    throw new CommonException("创建对象失败" + fieldClazz.getName());
                }
                declaredField.set(object, fieldObject);
            }
            preObjectMap.remove(clazz.getName());
            objectMap.put(clazz.getName(), object);
        } catch (Exception e) {
            throw new CommonException(e);
        }
        return object;
    }
}

