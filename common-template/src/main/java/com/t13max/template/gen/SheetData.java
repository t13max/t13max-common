package com.t13max.template.gen;


import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.t13max.template.exception.TemplateException;
import com.t13max.template.util.ExcelUtils;

import java.util.*;

/**
 * 一页的数据
 *
 * @author: t13max
 * @since: 16:02 2024/8/5
 */
public class SheetData {

    //excel名
    private final String excelName;
    //页签名
    private final String sheetName;
    //表头数据类型
    private final List<String> typeList = new ArrayList<>();
    //表头名字
    private final List<String> nameList = new ArrayList<>();
    //表数据
    private final List<Map<Object, Object>> dataMap = new ArrayList<>();
    //当前读取到多少行
    private int rowIndex;

    public SheetData(String excelName, String sheetName) {
        this.excelName = excelName;
        this.sheetName = sheetName;
    }

    /**
     * 处理表头
     *
     * @Author t13max
     * @Date 16:28 2024/8/5
     */
    public void processHeadSheet(Map<Integer, ReadCellData<?>> headMap, AnalysisContext context) {

        rowIndex = context.readSheetHolder().getRowIndex();

        switch (rowIndex) {
            case 0 -> {
                for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                    String stringValue = entry.getValue().getStringValue();
                    typeList.add(entry.getKey(), stringValue);
                }
            }

            case 1 -> {
                for (Map.Entry<Integer, ReadCellData<?>> entry : headMap.entrySet()) {
                    String stringValue = entry.getValue().getStringValue();
                    nameList.add(entry.getKey(), stringValue);
                }
            }
        }


    }

    /**
     * 处理表数据
     *
     * @Author t13max
     * @Date 16:34 2024/8/5
     */
    public void processDataSheet(LinkedHashMap<Integer, String> rowMap, AnalysisContext context) {
        rowIndex = context.readSheetHolder().getRowIndex();
        if (rowIndex <= GenerateConfig.HEAD_COUNT) {
            return;
        }
        final Map<Object, Object> data = new LinkedHashMap<>(16);


        for (int column = 0; column < typeList.size(); column++) {
            //类型
            String dataType = typeList.get(column);
            // 字段名
            String fieldName = nameList.get(column);

            if (dataType == null || dataType.isEmpty() || fieldName == null || fieldName.isEmpty()) {
                throw new TemplateException(excelName + "->" + sheetName + "表头信息定义错误");
            }
            String dataStr = rowMap.get(column);
            final Object value = ExcelUtils.getEasyExcelCellValue((ReadCellData) context.readSheetHolder().getCellMap().get(column), dataType, dataStr);
            data.put(fieldName, ExcelUtils.convertData(value, dataType));
        }
        if (!rowMap.isEmpty()) {
            dataMap.add(data);
        }

    }
}
