package ${package};

import java.util.*;
import com.t13max.template.ITemplate;
import com.alibaba.excel.annotation.ExcelProperty;
import com.t13max.template.converter.*;

/**
 * ${excelName}
 * ${entityNote}
 *
 * @author t13max-template
 *
 * 系统生成类 请勿修改
 */
public class ${entityName} implements ITemplate {

<#list props as prop>
<#if prop.note?? && (prop.note?length > 0)>
    /** ${prop.note} */
<#else>
    /** !!! 此参数无备注 !!! */
</#if>
<#if prop.convertor?? && (prop.convertor?length > 0)>
    @ExcelProperty(value = "${prop.name}", converter = ${prop.convertor})
<#else>
    @ExcelProperty("${prop.name}")
</#if>
    public final ${prop.type} ${prop.name};
</#list>

    public ${entityName}(${paramMethod}) {
    <#list props as prop>
        this.${prop.name} = ${prop.name};
    </#list>
    }

    @Override
    public int getId() {
        return id;
    }
}