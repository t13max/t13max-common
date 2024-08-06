package com.t13max.template;

import java.util.*;
import com.alibaba.excel.annotation.ExcelProperty;
import com.t13max.template.converter.ToIntListConverter;
import com.t13max.template.converter.ToIntMapConverter;

/**
 * hero.xlsx
 * 
 *
 * @author t13max-template
 *
 * 系统生成类 请勿修改
 */
public class HeroTemplate implements ITemplate {

    /** id */
    @ExcelProperty("id")
    public final int id;
    /** list */
    @ExcelProperty(value = "list", converter = ToIntListConverter.class)
    public final List<Integer> list;
    /** map */
    @ExcelProperty(value = "map", converter = ToIntMapConverter.class)
    public final Map<Integer,Integer> map;

    public HeroTemplate(int id, List<Integer> list, Map<Integer,Integer> map) {
        this.id = id;
        this.list = list;
        this.map = map;
    }

    @Override
    public int getId() {
        return id;
    }
}