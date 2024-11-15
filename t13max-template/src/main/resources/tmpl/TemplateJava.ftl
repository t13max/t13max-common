package ${package};

import java.util.*;
import com.t13max.template.ITemplate;

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
    public final ${prop.type} ${prop.name};
</#list>

    public ${entityName}(${paramMethod}) {
    <#list props as prop>
    <#if prop.type?? && (prop.type?contains("List"))>
        this.${prop.name} = Collections.unmodifiableList(${prop.name});
    <#else>
        this.${prop.name} = ${prop.name};
    </#if>
    </#list>
    }

    @Override
    public int getId() {
        return id;
    }
}