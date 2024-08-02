package com.t13max.template.helper;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.t13max.common.exception.CommonException;
import com.t13max.template.ITemplate;
import com.t13max.template.exception.TemplateException;
import com.t13max.template.listener.ReadDataListener;
import com.t13max.template.util.Log;
import org.apache.logging.log4j.core.util.JsonUtils;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: t13max
 * @since: 14:56 2024/5/23
 */
public abstract class TemplateHelper<T extends ITemplate> {

    protected String fileName;

    protected Map<Integer, T> DATA_MAP;

    protected Map<Integer, T> TEMP_DATA_MAP;

    public TemplateHelper(String fileName) {
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
    public void transfer() {
        this.DATA_MAP = TEMP_DATA_MAP;
        this.TEMP_DATA_MAP = null;
    }

    /**
     * 重新加载数据
     *
     * @Author t13max
     * @Date 15:03 2024/5/23
     */
    public void reload() {

        Log.template.info("{}从新加载开始!", fileName);

        if (!this.doLoad()) {
            //打印日志 告知没有reload成功
            Log.template.error("加载表失败! fileName={}", fileName);
        }
    }

    /**
     * 首次load
     *
     * @Author t13max
     * @Date 15:04 2024/5/23
     */
    public void load() {

        if (!doLoad()) {
            //直接抛出异常 不让起服
            throw new TemplateException("加载表失败");
        }
    }

    public boolean doLoad() {
        TEMP_DATA_MAP = new HashMap<>();

        ReadDataListener<T> tReadDataListener = new ReadDataListener<>();

        EasyExcel.read("/Users/antingbi/IdeaProjects/t13max-common/common-template/target/test-classes/" + fileName, this.getClazz(), tReadDataListener).headRowNumber(2).sheet("hero").doRead();

        List<T> iTemplates = tReadDataListener.getList();
        if (iTemplates == null || iTemplates.isEmpty()) {
            return false;
        }
        iTemplates.forEach(e -> TEMP_DATA_MAP.put(e.getId(), e));
        return true;
    }

    /**
     * 根据id获取数据
     *
     * @Author t13max
     * @Date 15:02 2024/5/23
     */
    public T getTemplate(int id) {
        return DATA_MAP.get(id);
    }

    /**
     * 获取所有
     *
     * @Author t13max
     * @Date 15:23 2024/5/23
     */
    public Collection<T> getAll() {
        return DATA_MAP.values();
    }

    /**
     * 专门用于校验用的获取所有数据
     *
     * @Author t13max
     * @Date 20:31 2024/5/27
     */
    public Collection<T> getTempAll() {
        return TEMP_DATA_MAP.values();
    }

}
