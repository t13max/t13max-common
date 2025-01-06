package com.t13max.util.collection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 万能参数
 *
 * @author t13max
 * @since 16:10 2025/1/6
 */
public class Param {
    private static final String KEY_SINGLE = "KEY_SINGLE_PARAM";

    private final Map<String, Object> dataMap;

    public Param() {
        dataMap = new HashMap<>();
    }

    public Param(Param param) {
        this.dataMap = new HashMap<>();
        for (String key : param.dataMap.keySet()) {
            this.dataMap.put(key, param.dataMap.get(key));
        }
    }

    public Param(Object... params) {
        this.dataMap = new HashMap<>();
        if (params != null && params.length != 0) {
            if (params.length == 1) {
                this.put("KEY_SINGLE_PARAM", params[0]);
            } else {
                int len = params.length;

                for (int i = 0; i < len; i += 2) {
                    String key = (String) params[i];
                    Object val = params[i + 1];
                    this.put(key, val);
                }
            }

        }
    }

    public Param put(String key, Object value) {
        this.dataMap.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <K> K get(String key) {
        return (K) this.dataMap.get(key);
    }

    public <K> K get() {
        return this.get("KEY_SINGLE_PARAM");
    }

    public boolean getBoolean(String key) {
        return (Boolean) this.get(key);
    }

    public boolean getBoolean() {
        return (Boolean) this.get();
    }

    public int getInt(String key) {
        return (Integer) this.get(key);
    }

    public int getInt() {
        return (Integer) this.get();
    }

    public long getLong(String key) {
        return (Long) this.get(key);
    }

    public long getLong() {
        return (Long) this.get();
    }

    public String getString(String key) {
        return (String) this.get(key);
    }

    public String getString() {
        return (String) this.get();
    }

    public int size() {
        return this.dataMap.size();
    }

    public boolean isEmpty() {
        return this.dataMap.isEmpty();
    }

    public void clear() {
        this.dataMap.clear();
    }

    public boolean containsKey(String key) {
        return this.dataMap.containsKey(key);
    }

    public Object[] toArray() {
        Object[] arr;
        if (this.dataMap.isEmpty()) {
            arr = new Object[0];
        } else if (this.dataMap.size() == 1 && this.dataMap.containsKey("KEY_SINGLE_PARAM")) {
            arr = new Object[]{this.dataMap.get("KEY_SINGLE_PARAM")};
        } else {
            arr = new Object[this.dataMap.size() * 2];
            int index = 0;

            Map.Entry<String, Object> e;
            for (Iterator<Map.Entry<String, Object>> var3 = this.dataMap.entrySet().iterator(); var3.hasNext(); arr[index++] = e.getValue()) {
                e = var3.next();
                arr[index++] = e.getKey();
            }
        }

        return arr;
    }

    public Set<String> keySet() {
        return this.dataMap.keySet();
    }

    public String toString() {
        return this.dataMap.toString();
    }

}
