package com.t13max.template.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.t13max.template.ITemplate;
import com.t13max.template.converter.ToIntListConverter;
import com.t13max.template.converter.ToIntMapConverter;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author: t13max
 * @since: 11:20 2024/8/2
 */
@Data
public class HeroTemplate implements ITemplate {

    @ExcelProperty("id")
    private int id;

    @ExcelProperty(value = "list", converter = ToIntListConverter.class)
    private List<Integer> list;

    @ExcelProperty(value = "map", converter = ToIntMapConverter.class)
    private Map<Integer, Integer> map;
}
