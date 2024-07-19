package com.t13max.ai.behavior4j.plugins;


import groovy.lang.GroovyObject;

/**
 * @Author t13max
 * @Date 13:48 2024/5/23
 */
public interface ScriptDataSource {
    GroovyObject loadScript(String name);
}
