package com.t13max.template.gen;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * 一个excel
 *
 * @author: t13max
 * @since: 16:24 2024/8/5
 */
@Getter
public class ExcelData {

    private String excelName;

    /**
     * Sheets数据集合
     */
    private final Map<String, SheetData> sheetDataMap = new HashMap<>(16);
}
