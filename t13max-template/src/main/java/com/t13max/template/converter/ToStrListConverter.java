package com.t13max.template.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.util.Arrays;
import java.util.List;

/**
 * @author: t13max
 * @since: 11:13 2024/8/2
 */
public class ToStrListConverter implements Converter<List<String>> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return List.class;
    }

    @Override
    public com.alibaba.excel.enums.CellDataTypeEnum supportExcelTypeKey() {
        return com.alibaba.excel.enums.CellDataTypeEnum.STRING;
    }

    @Override
    public List<String> convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return Arrays.stream(cellData.getStringValue().split(";")).toList();
    }

}
