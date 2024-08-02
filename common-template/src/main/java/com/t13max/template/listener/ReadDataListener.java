package com.t13max.template.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.t13max.template.ITemplate;
import com.t13max.template.helper.TemplateHelper;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: t13max
 * @since: 11:44 2024/8/2
 */
public class ReadDataListener<T extends ITemplate> implements ReadListener<T> {

    @Getter
    private final List<T> list = new ArrayList<>();

    @Override
    public void invoke(T t, AnalysisContext analysisContext) {
        list.add(t);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}