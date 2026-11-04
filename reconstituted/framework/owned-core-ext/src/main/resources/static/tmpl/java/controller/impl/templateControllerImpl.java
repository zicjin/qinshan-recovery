package ${data.packageName}.controller.impl;

import org.springframework.stereotype.Controller;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import ${data.packageName}.controller.${data.javaObjectName}Controller;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;

@Controller
@GikamBean
public class ${data.javaObjectName}ControllerImpl implements ${data.javaObjectName}Controller {

<#list data.funcList as func>
<#if func == "edit">
    @Log(value = "${data.tableDesc}编制列表页", type = LogType.CONTROLLER)
    @Override
    public String editListPage() {
        return "${data.htmlPath}/${data.htmlFileName}-edit-list";
    }
<#elseif func == "detail">
    @Log(value = "${data.tableDesc}详情页", type = LogType.CONTROLLER)
    @Override
    public String detailPage() {
        return "${data.htmlPath}/${data.htmlFileName}-detail";
    }
<#elseif func == "audit">
    @Log(value = "${data.tableDesc}审核列表页", type = LogType.CONTROLLER)
    @Override
    public String auditListPage() {
        return "${data.htmlPath}/${data.htmlFileName}-audit-list";
    }
<#elseif func == "search">
    @Log(value = "${data.tableDesc}查询列表页", type = LogType.CONTROLLER)
    @Override
    public String searchListPage() {
        return "${data.htmlPath}/${data.htmlFileName}-search-list";
    }
<#elseif func == "undo">
    @Log(value = "${data.tableDesc}回退列表页", type = LogType.CONTROLLER)
    @Override
    public String undoListPage() {
        return "${data.htmlPath}/${data.htmlFileName}-undo-list";
    }
<#elseif func == "choose">
    @Log(value = "${data.tableDesc}选择列表页", type = LogType.CONTROLLER)
    @Override
    public String choosePage() {
        return "${data.htmlPath}/${data.htmlFileName}-choose-list";
    }
</#if>

</#list>
}
