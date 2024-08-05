package ${package};

import java.util.*;
import java.util.Map.Entry;

import com.longtugame.amber.npc.common.application.LogServer;
import org.apache.commons.lang3.StringUtils;
import org.gof.core.support.ConfigJSON;
import com.longtugame.amber.npc.logic.support.SysException;
import com.longtugame.amber.npc.logic.support.ConfBase;
import com.longtugame.amber.npc.logic.support.OrderBy;
import com.longtugame.amber.npc.logic.support.OrderByField;
import com.longtugame.amber.npc.logic.support.Utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * ${excelName}
 * ${entityNote}
 *
 * @author t13max-template
 *
 * 系统生成类 请勿修改
 */
@ConfigJSON
public class ${entityName} implements ITemplate {

<#list props as prop>
<#if prop.note?? && (prop.note?length > 0)>
    /** ${prop.note} */
<#else>
    /** !!! 此参数无备注 !!! */
</#if>
    public final ${prop.type} ${prop.name};
</#list>

    /**
     * 属性关键字
     */
    public static final class K {
    <#list props as prop>
    <#if prop.note?? && (prop.note?length > 0)>
        /** ${prop.note} */
    <#else>
        /** !!! 此参数无备注 !!! */
    </#if>
        public static final String ${prop.name} = "${prop.name}";
    </#list>
    }

    public ${entityName}(${paramMethod}) {
    <#list props as prop>
        this.${prop.name} = ${prop.name};
    </#list>
    }

    public ${entityName}(${entityName} other) {
    <#list props as prop>
        this.${prop.name} = other.${prop.name};
    </#list>
    }

    /**
     * 热加载全部数据
     */
    public static void reLoad() {
        DATA.reLoad();
    }

    /**
    * 通过Id列表热加载
    */
    public static void reLoadByIds(Collection<${snDataType}> idList) {
        DATA.reLoadByIds(idList);
    }

    /**
     * 通过Sn获取数据实体对象
     */
    public static ${entityName} get(${snDataType} sn) {
        return DATA.getMap().get(sn);
    }

    /**
     * 通过Sn获取数据实体对象，若数据对象为空则打印错误日志
     */
    public static ${entityName} getNotNull(${snDataType} sn) {
        ${entityName} conf = get(sn);
        if (conf == null) {
            if (LogServer.conf.isErrorEnabled()) {
                LogServer.conf.error("${entityName}配置表缺失, sn = " + sn, new Throwable());
            }
        }
        return conf;
    }

    /**
     * 获取全部数据
     */
    public static Collection<${entityName}> findAll() {
        return DATA.getList();
    }

    /**
     * 通过属性获取单条数据
     */
    @Deprecated
    public static ${entityName} getBy(Object... params) {
        List<${entityName}> list = utilBase(params);
        return list.isEmpty() ? null : list.get(0);
    }

    /**
     * 通过属性获取数据集合
     */
    @Deprecated
    public static List<${entityName}> findBy(Object... params) {
        return utilBase(params);
    }

<#list props as prop>
    <#if prop.upgrade??>
    /**
    * 获取带有升级的属性: ${prop.name} 
    * ${prop.note}
    */
    public ${prop.type} get_${prop.name}_upgrade(int level) {
        ${entityName}Upgrade confUpgrade = ${entityName}Upgrade.getByBaseSnAndLv(sn, level);

        return  confUpgrade == null ? ${prop.name} : confUpgrade.${prop.name};
    }
    </#if>
</#list>

<#if indexes?? && (indexes?size > 0)>

    // ===========================================================================
    // 组合索引方法
    // ===========================================================================

    /**
     * 插入索引列表Map
     */
    private static void putMap(Map<String, List<${entityName}>> map, ${entityName} value, Object... keys) {
        String key = makeGroupKey(keys);

        List<${entityName}> list = map.computeIfAbsent(key, k -> new ArrayList<>());
        list.add(value);
    }

</#if>
<#list indexes as index>
    public static List<${entityName}> findBy${index.name}(${index.methodParam}) {
        String key = ConfBase.makeGroupKey(${index.params});
        List<${entityName}> results = DATA.get${index.name}().get(key);
        return results == null ? new ArrayList<>() : Collections.unmodifiableList(results);
    }

    public static ${entityName} getBy${index.name}(${index.methodParam}) {
        String key = ConfBase.makeGroupKey(${index.params});
        if (DATA.get${index.name}().get(key) == null) {
            return null;
        }

        List<${entityName}> results = Collections.unmodifiableList(DATA.get${index.name}().get(key));
        return results.isEmpty() ? null : results.get(0);
    }

</#list>
    /**
     * 通过属性获取数据集合
     * tips: 支持多条件查询排序
     */
    public static List<${entityName}> utilBase(Object... params) {
        List<Object> settings = Utils.ofList(params);
        // 参数数量
        int len = settings.size();
        // 参数必须成对出现
        if (len % 2 != 0) {
            throw new SysException("查询参数必须成对出现, 参数列表 = {}", settings);
        }

        // 过滤条件
        final Map<String, Object> paramsFilter = new LinkedHashMap<>();
        // 排序规则
        final List<OrderByField> paramsOrder = new ArrayList<>();

        // 处理成对参数
        for (int i = 0; i < len; i += 2) {
            String key = (String) settings.get(i);
            Object val = settings.get(i + 1);


            if (val instanceof OrderBy) {
                // 参数 排序规则
                paramsOrder.add(new OrderByField(key, (OrderBy) val));
            } else {
                // 参数 过滤条件
                paramsFilter.put(key, val);
            }
        }

        // 返回结果
        List<${entityName}> result = new ArrayList<>();
        try {
            // 通过条件获取结果
            for (${entityName} c : DATA.getList()) {
                // 本行数据是否符合过滤条件
                boolean bingo = true;

                // 判断过滤条件
                for (Entry<String, Object> p : paramsFilter.entrySet()) {
                    // 实际结果
                    Object valTrue = c.getFieldValue(p.getKey());
                    // 期望结果
                    Object valWish = p.getValue();

                    // 有不符合过滤条件的
                    if (!valWish.equals(valTrue)) {
                        bingo = false;
                        break;
                    }
                }

                // 记录符合结果
                if (bingo) {
                    result.add(c);
                }
            }
        } catch (Exception e) {
            throw new SysException(e);
        }

        // 对结果进行排序
        result.sort((a, b) -> a.compareTo(b, paramsOrder));
        return result;
    }

    /**
     * 取得属性值
     */
    @SuppressWarnings("unchecked")
    public <T> T getFieldValue(String key) {
        Object value = null;
        switch (key) {
        <#list props as prop>
            case "${prop.name}": {
                value = this.${prop.name};
                break;
            }
        </#list>
            default: {
                break;
            }
        }

        return (T) value;
    }

    /**
     * 比较函数
     *
     * @param cell   比较的对象
     * @param params 自定义排序字段
     */
    public int compare(${entityName} cell, Object... params) {
        List<Object> settings = Utils.ofList(params);
        // 参数数量
        int len = settings.size();
        // 参数必须成对出现
        if (len % 2 != 0) {
            throw new SysException("查询参数必须成对出现, 参数列表 = {}", settings);
        }

        // 排序规则
        List<OrderByField> paramsOrder = new ArrayList<>();

        // 处理成对参数
        for (int i = 0; i < len; i += 2) {
            String key = (String) settings.get(i);
            Object val = settings.get(i + 1);

            // 参数 排序规则
            if (val instanceof OrderBy) {
                paramsOrder.add(new OrderByField(key, (OrderBy) val));
            }
        }

        return compareTo(cell, paramsOrder);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private int compareTo(${entityName} cell, List<OrderByField> paramsOrder) {
        try {
            for (OrderByField e : paramsOrder) {
                // 两方字段值
                Comparable va = this.getFieldValue(e.getKey());
                Comparable vb = cell.getFieldValue(e.getKey());

                // 值排序结果
                int compareResult = va.compareTo(vb);

                // 相等时 根据下一个值进行排序
                if (va.compareTo(vb) == 0) {
                    continue;
                }

                // 配置排序规则: ASC 正序 / DESC 倒序
                return e.getOrderBy() == OrderBy.ASC ? compareResult : -1 * compareResult;
            }
        } catch (Exception e) {
            throw new SysException(e);
        }

        return 0;
    }

    /**
     * 数据集
     * 单独提出来也是为了做数据延迟初始化
     * 避免启动遍历类时，触发了static静态块
     */
    private static final class DATA {

        /** 全部数据 */
        private static volatile Map<${snDataType}, ${entityName}> map;

        <#list indexes as index>
        /** ${index.name} 索引目录 */
        private static volatile Map<String, List<${entityName}>> map${index.name};

        </#list>
        private static final String name = "${entityName}";

        public static void clearCache() {
            synchronized (DATA.class) {
                map = null;
            }
        }

        /**
         * 获取数据的值集合
         */
        public static Collection<${entityName}> getList() {
            return getMap().values();
        }

        /**
         * 获取Map类型数据集合
         */
        public static Map<${snDataType}, ${entityName}> getMap() {
            // 延迟初始化
            if (map == null) {
                synchronized (DATA.class) {
                    if (map == null) {
                        init();
                    }
                }
            }

            return map;
        }

    <#list indexes as index>
        public static Map<String, List<${entityName}>> get${index.name}() {
            getMap();
            return map${index.name};
        }

    </#list>
        /**
         * 初始化数据
         */
        private static void init() {
            Map<${snDataType}, ${entityName}> dataMap = new HashMap<>();
        <#list indexes as index>
            Map<String, List<${entityName}>> dataMap${index.name} = new HashMap<>();
        </#list>

            // JSON数据
            String confJSON = readConfFile(name);
            if (!StringUtils.isBlank(confJSON)) {
                // 填充实体数据
                JSONArray confs = (JSONArray) JSONArray.parse(confJSON);
                for (int i = 0; i < Objects.requireNonNull(confs).size(); i++) {
                    JSONObject conf = confs.getJSONObject(i);
                    ${entityName} object = new ${entityName}(${paramInit});
                    dataMap.put(conf.get${snDataType}("sn"), object);
                <#list indexes as index>
                    putMap(dataMap${index.name}, object, ${index.paramInit});
                </#list>
                }
            } else {
                if (LogServer.conf.isWarnEnabled()) {
                    LogServer.conf.warn("${entityName}配置表为空");
                }
            }

            // 保存数据
            map = Collections.unmodifiableMap(dataMap);
        <#list indexes as index>
            map${index.name} = Collections.unmodifiableMap(dataMap${index.name});
        </#list>
        }

        /**
         * 重新加载全部数据
         */
        public static void reLoad() {
            synchronized (DATA.class) {
                map = null;
                init();
            }
        }

        /**
         * 通过Id列表重新加载数据
         */
        public static void reLoadByIds(Collection<${snDataType}> idList) {
            if (map == null) {
                synchronized (DATA.class) {
                    if (map == null) {
                        init();
                        return;
                    }
                }
            }
        
            Map<${snDataType}, ${entityName}> dataMap = new HashMap<>(map);

            // JSON数据
            String confJSON = readConfFile(name);
            if (StringUtils.isBlank(confJSON)) {
                if (LogServer.conf.isErrorEnabled()) {
                    LogServer.conf.error("${entityName}配置表为空");
                }
                return;
            }

            // 填充实体数据
            JSONArray confs = (JSONArray) JSONArray.parse(confJSON);
            for (int i = 0; i < Objects.requireNonNull(confs).size(); i++) {
                JSONObject conf = confs.getJSONObject(i);
                if (idList.contains(conf.get${snDataType}("sn"))) {
                    dataMap.computeIfPresent(conf.get${snDataType}("sn"), (k, v) -> {
                        v = new ${entityName}(${paramInit});
                        return v;
                    });
                }
            }

            synchronized (DATA.class) {
                if (map == null) {
                    init();
                } else {
                    for (${snDataType} sn : idList) {
                        if (!dataMap.containsKey(sn)) {
                            if (LogServer.conf.isErrorEnabled()) {
                                LogServer.conf.error("${entityName}配置表找不到要更新的行, sn = {}", sn);
                            }
                            throw new RuntimeException("${entityName}配置表找不到要更新的行");
                        }
                    }
                    
                    // 保存数据
                    map = Collections.unmodifiableMap(dataMap);
                }
            }
        }
    }
}