package com.t13max.template.util;

import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.util.StringUtils;
import com.t13max.common.util.Log;
import com.t13max.template.enums.DataTypeEnum;
import com.t13max.template.exception.TemplateException;
import com.t13max.util.StringUtil;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author: t13max
 * @since: 16:51 2024/8/5
 */
@UtilityClass
public class ExcelUtils {

    public Object getEasyExcelCellValue(ReadCellData readCellData, String dataType, String dataStr) {
        // 设置默认值
        if (readCellData == null || StringUtils.isEmpty(dataStr)) {
            return "";
        }

        // 处理数据
        DataTypeEnum type = DataTypeEnum.of(dataType);
        switch (readCellData.getType()) {
            // 数字类型
            case NUMBER: {
                return getNumberValue(type, readCellData.getNumberValue().doubleValue(), readCellData.getDataFormatData().getFormat());
            }
            // 字符串类型
            case STRING: {
                return readCellData.getStringValue();
            }

            default: {
                throw new TemplateException("未知的数据类型");
            }
        }
    }

    public static Object convertData(Object value, String dataType) throws TemplateException {
        DataTypeEnum type = DataTypeEnum.of(dataType);
        if (type == null) {
            throw new TemplateException("未知的数据类型, dataType="+dataType);
        }

        String str = String.valueOf(value);
        if (str == null || str.trim().isEmpty()) {
            return getDefaultCellValue(dataType);
        }

        try {
            switch (type) {
                case INT: {
                    if (str.contains("0x")) {
                        return convert0xData(str);
                    }
                    double dValue = Double.parseDouble(str);
                    if (dValue > Integer.MAX_VALUE) {
                        throw new TemplateException("Int类型超出最大值");
                    }
                    return (int) dValue;
                }
                case FLOAT: {
                    return Float.parseFloat(str);
                }
                case STRING_ARR: {
                    return StringUtil.getStrList(str);
                }
                case INT_ARRAY: {
                    return StringUtil.getIntList(str);
                }
                case FLOAT_ARR: {
                    return StringUtil.getFloatList(str);
                }
                case MAP: {
                    return StringUtil.getIntMap(str);
                }
                case STRING:
                    return value;
                default: {
                    throw new TemplateException("未知的数据类型");
                }
            }
        } catch (Exception e) {
            Log.TEMPLATE.error("转换出错, value={}", value,e);
            throw new TemplateException("数据解析出错");
        }
    }

    /**
     * 将[[0x10001010, 0x10002010], [0x10002010]] 这种数据变为实际的数值，只支持10位的！！
     * tips: 用于解决json验证问题，以及数据读取时问题
     */
    private static String convert0xData(String str) {
        if (!str.contains("0x")) {
            return str;
        }

        String[] tmp = str.split(",");
        for (String s : tmp) {
            // 获得 0x1000101 字符串
            String s1 = s.substring(s.indexOf("0x"), (s.indexOf("0x") + 10));
            // 将 0x1000101 解析成数字后，再转成替换后的字符串
            String s2 = String.valueOf(Integer.decode(s1));
            // 替换
            str = str.replaceAll(s1, s2);
        }

        return str;
    }

    private static Object getNumberValue(final DataTypeEnum eDataType, final double doubleValue, final String dataFormatString) {
        if (eDataType == DataTypeEnum.INT_ARRAY && dataFormatString.equals("#,##0")) {
            return new DecimalFormat(dataFormatString).format(doubleValue);
        }
        return num2String(doubleValue);
    }

    public static Object getDefaultCellValue(String dataType) {
        return getDefaultCellValue(DataTypeEnum.of(dataType));
    }

    /**
     * 去除科学计数法，并去除末尾无意义的0
     *
     * @Author t13max
     * @Date 17:09 2024/8/5
     */
    private static String num2String(final double numericCellValue) {
        return new BigDecimal(String.valueOf(numericCellValue), mathContext).stripTrailingZeros().toPlainString();
    }

    /**
     * excel单元格只支持15位数字
     */
    private static final MathContext mathContext = new MathContext(15, RoundingMode.HALF_UP);

    public static Object getDefaultCellValue(DataTypeEnum type) {
        return type.getDefaultValue();
    }
}
