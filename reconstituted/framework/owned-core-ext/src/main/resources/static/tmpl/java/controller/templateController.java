package ${data.packageName}.controller;

import org.springframework.web.bind.annotation.RequestMapping;

<#if data.funcList ?? && data.funcList?seq_contains("edit") > 
import com.sunwayworld.framework.controller.GenericEditListPageController;
</#if>
<#if data.funcList ?? && data.funcList?seq_contains("detail") > 
import com.sunwayworld.framework.controller.GenericDetailPageController;
</#if>
<#if data.funcList ?? && data.funcList?seq_contains("audit") > 
import com.sunwayworld.framework.controller.GenericAuditListPageController;
</#if>
<#if data.funcList ?? && data.funcList?seq_contains("search") > 
import com.sunwayworld.framework.controller.GenericSearchListPageController;
</#if>
<#if data.funcList ?? && data.funcList?seq_contains("choose") > 
import com.sunwayworld.framework.controller.GenericChoosePageController;
</#if>
<#if data.funcList ?? && data.funcList?seq_contains("undo") > 
import com.sunwayworld.framework.controller.GenericUndoListPageController;
</#if>

@RequestMapping("/secure/${data.controllerPath}/page")
public interface ${data.javaObjectName}Controller extends GenericEditListPageController<#if data.funcList ?? && data.funcList?seq_contains("detail") >, GenericDetailPageController</#if><#if data.funcList ?? && data.funcList?seq_contains("audit") >, GenericAuditListPageController</#if><#if data.funcList ?? && data.funcList?seq_contains("search") >, GenericSearchListPageController</#if><#if data.funcList ?? && data.funcList?seq_contains("choose") >, GenericChoosePageController</#if><#if data.funcList ?? && data.funcList?seq_contains("undo") >, GenericUndoListPageController</#if> {
}
