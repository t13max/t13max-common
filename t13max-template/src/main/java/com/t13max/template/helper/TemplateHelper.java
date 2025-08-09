package com.t13max.template.helper;

import com.t13max.common.util.Log;
import com.t13max.template.ITemplate;
import com.t13max.template.exception.TemplateException;
import com.t13max.template.util.JsonUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: t13max
 * @since: 14:56 2024/5/23
 */
public abstract class TemplateHelper<T extends ITemplate> {

    private final static String PATH_ENV = "TEMPLATE_PATH";

    protected String fileName;

    protected volatile Map<Integer, T> DATA_MAP;

    protected volatile Map<Integer, T> TEMP_DATA_MAP;

    protected TemplateHelper(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 表校验
     *
     * @Author t13max
     * @Date 15:01 2024/5/23
     */
    public abstract boolean configCheck();

    /**
     * 获取class类型
     *
     * @Author t13max
     * @Date 15:10 2024/5/23
     */
    public abstract <T extends ITemplate> Class<T> getClazz();

    /**
     * 临时数据转正
     *
     * @Author t13max
     * @Date 15:01 2024/5/23
     */
    public final void transfer() {
        if (TEMP_DATA_MAP == null) {
            return;
        }
        this.DATA_MAP = TEMP_DATA_MAP;
        this.TEMP_DATA_MAP = null;
        transferAfter();
    }

    protected void transferAfter() {

    }

    /**
     * 重新加载数据
     *
     * @Author t13max
     * @Date 15:03 2024/5/23
     */
    public final void reload() {
        reload(null);
    }

    public final void reload(String path) {

        Log.TEMPLATE.info("{}从新加载开始!", fileName);

        if (!this.doLoad(path)) {
            //打印日志 告知没有reload成功
            Log.TEMPLATE.error("加载表失败! fileName={}", fileName);
        }

        if (!this.loadAfter()) {
            //打印日志 告知没有reload成功
            Log.TEMPLATE.error("加载表失败! fileName={}", fileName);
        }
    }

    /**
     * 首次load
     *
     * @Author t13max
     * @Date 15:04 2024/5/23
     */
    public final void load() {
        if (DATA_MAP != null) {
            return;
        }
        if (!doLoad()) {
            //直接抛出异常 不让起服
            throw new TemplateException("加载表失败");
        }
        if (!loadAfter()) {
            throw new TemplateException("加载表失败");
        }
    }

    private boolean doLoad() {
        return doLoad(null);
    }

    private boolean doLoad(String path) {
        TEMP_DATA_MAP = new HashMap<>();
        if (path == null) {
            path = System.getenv(PATH_ENV);
        }
        List<T> iTemplates;
        if (path == null) {
            iTemplates = JsonUtils.readInJarJson(fileName, this.getClazz());
        } else {
            iTemplates = JsonUtils.readOutJson(path + "/" + fileName, this.getClazz());
        }
        if (iTemplates == null || iTemplates.isEmpty()) {
            return false;
        }
        iTemplates.forEach(e -> TEMP_DATA_MAP.put(e.getId(), e));
        return true;
    }


    protected boolean loadAfter() {
        return true;
    }

    /**
     * 根据id获取数据
     *
     * @Author t13max
     * @Date 15:02 2024/5/23
     */
    public final T getTemplate(int id) {
        return DATA_MAP.get(id);
    }

    /**
     * 获取所有
     *
     * @Author t13max
     * @Date 15:23 2024/5/23
     */
    public final Collection<T> getAll() {
        return DATA_MAP.values();
    }

    /**
     * 专门用于校验用的获取所有数据
     *
     * @Author t13max
     * @Date 20:31 2024/5/27
     */
    public final Collection<T> getTempAll() {
        return TEMP_DATA_MAP.values();
    }

}
