package com.t13max.ai.behavior4j.plugins;


import com.t13max.ai.utils.GroovyScriptManager;
import groovy.lang.GroovyObject;

/**
 * @Author t13max
 * @Date 13:48 2024/5/23
 */
public class DefaultDataSource implements ScriptDataSource {

    @Override
    public GroovyObject loadScript(String name) {
        return GroovyScriptManager.getInstance().loadScript(name);
    }
}
