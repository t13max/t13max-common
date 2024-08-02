package com.t13max.template.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.listener.ReadListener;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 表头读取监听器
 *
 * @author: t13max
 * @since: 11:44 2024/8/2
 */
@Getter
public class ReadHeadListener implements ReadListener<LinkedHashMap<Integer, String>> {

    private List<String> typeList = new ArrayList<>();

    private List<String> nameList = new ArrayList<>();

    private boolean stop;

    @Override
    public void invokeHead(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {
        for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
            String stringValue = entry.getValue().getStringValue();
            typeList.add(entry.getKey(), stringValue);
        }
    }

    @Override
    public void invoke(LinkedHashMap<Integer, String> row, AnalysisContext analysisContext) {
        for (Map.Entry<Integer, String> entry : row.entrySet()) {
            nameList.add(entry.getKey(), entry.getValue());
        }
        stop = true;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    @Override
    public boolean hasNext(AnalysisContext context) {
        return !stop;
    }
}
