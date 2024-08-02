package com.t13max.template.converter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.t13max.template.exception.TemplateException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: t13max
 * @since: 11:13 2024/8/2
 */
public class ToIntMapConverter implements Converter<Map<Integer, Integer>> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return Map.class;
    }

    @Override
    public com.alibaba.excel.enums.CellDataTypeEnum supportExcelTypeKey() {
        return com.alibaba.excel.enums.CellDataTypeEnum.STRING;
    }

    @Override
    public Map<Integer, Integer> convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        Map<Integer, Integer> result = new HashMap<>();
        String stringValue = cellData.getStringValue();
        String[] split = stringValue.split(";");
        for (String kv : split) {
            String[] split1 = kv.split(",");
            if (split1.length != 2) {
                throw new TemplateException("配置错误! error=" + stringValue);
            }
            result.put(Integer.parseInt(split1[0]), Integer.parseInt(split1[1]));
        }
        return result;
    }

}
