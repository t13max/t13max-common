package com.t13max.ai.utils;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author t13max
 * @Date 13:50 2024/5/23
 */
public class GroovyScriptManager {
    private static volatile GroovyScriptManager instance;

    private GroovyClassLoader classLoader;

    private Map<String, Class<?>> classes = new ConcurrentHashMap<>();

    private GroovyScriptManager() {
        ClassLoader parentClassLoader = this.getClass().getClassLoader();
        classLoader = new GroovyClassLoader(parentClassLoader);
    }

    public static GroovyScriptManager getInstance() {
        GroovyScriptManager loader = instance;
        if (loader == null) {
            synchronized (GroovyClassLoader.class) {
                loader = instance;
                if (loader == null) {
                    instance = loader = new GroovyScriptManager();
                }
            }
        }

        return loader;
    }

    public GroovyObject loadScript(String filePath) {
        try {
            Class<?> groovyClass = classes.get(filePath);
            if (groovyClass == null) {
                groovyClass = classLoader.parseClass(new File(filePath));
                classes.put(filePath, groovyClass);
            }
            return (GroovyObject) groovyClass.newInstance();
        } catch (IOException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }

        return null;
    }

}
