package com.t13max.ai.data;

import com.t13max.ai.behavior4j.BTNode;
import groovy.lang.GroovyObject;
import lombok.Data;

/**
 * @Author t13max
 * @Date 13:49 2024/5/23
 */
@Data
public class ScriptDefine {
    private String path;

    private GroovyObject scriptObj;

    public ScriptDefine(GroovyObject scriptObj) {
        this.scriptObj = scriptObj;
    }

    public ScriptDefine() {

    }

    public <T> BTNode<T> getNode(int id) {
        if (scriptObj == null) {
            scriptObj = BehaviorTreeManager.getInstance().loadGroovy(path);
        }
        @SuppressWarnings("unchecked")
        BTNode<T> node = (BTNode<T>) this.scriptObj.invokeMethod("getNode", id);
        node.setScript(path);

        return node;
    }
}
