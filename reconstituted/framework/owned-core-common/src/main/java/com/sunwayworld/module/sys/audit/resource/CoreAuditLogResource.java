/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogActivityBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnVerticalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogHorizontalRepresentation;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemHorizontalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemVerticalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogTraceOverviewDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogVerticalRepresentation;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/secure/core/module/sys/audit/audit-logs"})
public interface CoreAuditLogResource {
    @Log(value="\u67e5\u8be2\u5ba1\u8ba1\u8bb0\u5f55\u6839\u8282\u70b9\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(method={RequestMethod.GET})
    public CoreAuditLogDTO selectAuditLogRoot(@RequestParam String var1, @RequestParam String var2);

    @Log(value="\u6839\u636e\u5f53\u524d\u8282\u70b9\u6216\u7236\u8282\u70b9\u7684\u5ba1\u8ba1\u8bb0\u5f55\u7f16\u7801\u6765\u67e5\u8be2\u5ba1\u8ba1\u8bb0\u5f55\u7684\u5355\u636e", type=LogType.SELECT)
    @RequestMapping(value={"/items"}, method={RequestMethod.GET})
    public List<CoreAuditLogItemDTO> selectAuditLogItemList(@RequestParam(required=false) String var1, @RequestParam String var2, @RequestParam(required=false) Long var3, @RequestParam(required=false) Long var4);

    @Log(value="\u6839\u636e\u5ba1\u8ba1\u8bb0\u5f55\u7f16\u7801\u6765\u67e5\u8be2\u5f53\u524d\u5355\u636e\u7684\u5ba1\u8ba1\u8bb0\u5f55\u4fe1\u606f\uff08\u7ad6\u7248\uff09", type=LogType.SELECT)
    @RequestMapping(value={"/{auditLogId}/vertical-activities"}, method={RequestMethod.GET})
    public CoreAuditLogItemVerticalDTO selectAuditLogItemActivityVertical(@PathVariable Long var1);

    @Log(value="\u6839\u636e\u5ba1\u8ba1\u8bb0\u5f55\u64cd\u4f5c\u7f16\u7801\u6765\u67e5\u8be2\u5b57\u6bb5\u7684\u53d8\u66f4\u4fe1\u606f\uff08\u7ad6\u7248\uff09", type=LogType.SELECT)
    @RequestMapping(value={"/activities/{activityId}/vertical-columns"}, method={RequestMethod.GET})
    public List<CoreAuditLogColumnVerticalDTO> selectAuditLogItemActivityColumnVerticalList(@PathVariable Long var1);

    @Log(value="\u6839\u636e\u5ba1\u8ba1\u8bb0\u5f55\u7f16\u7801\u6765\u67e5\u8be2\u5f53\u524d\u5355\u636e\u7684\u5ba1\u8ba1\u8bb0\u5f55\u4fe1\u606f\uff08\u6a2a\u7248\uff09", type=LogType.SELECT)
    @RequestMapping(value={"/{auditLogId}/horizontal-activities"}, method={RequestMethod.GET})
    public CoreAuditLogItemHorizontalDTO selectAuditLogItemActivityHorizontal(@PathVariable Long var1);

    @Log(value="\u67e5\u8be2\u5ba1\u8ba1\u8bb0\u5f55(\u7ad6\u7248)", type=LogType.SELECT)
    @RequestMapping(value={"/vertical"}, method={RequestMethod.GET})
    public CoreAuditLogVerticalRepresentation selectAuditLogVertical(@RequestParam String var1, @RequestParam String var2);

    @Log(value="\u67e5\u8be2\u5ba1\u8ba1\u8bb0\u5f55(\u6a2a\u7248)", type=LogType.SELECT)
    @RequestMapping(value={"/horizontal"}, method={RequestMethod.GET})
    public CoreAuditLogHorizontalRepresentation selectAuditLogHorizontal(@RequestParam String var1, @RequestParam String var2);

    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u5173\u952e\u6570\u636e\u6982\u89c8", type=LogType.SELECT)
    @RequestMapping(value={"/queries/trace-overview"}, method={RequestMethod.POST})
    public Page<CoreAuditLogTraceOverviewDTO> selectTraceOverview(RestJsonWrapperBean var1);

    @Log(value="\u5ba1\u8ba1\u8ddf\u8e2a\u6570\u636e\u53ca\u660e\u7ec6\u8868\u8be6\u60c5\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/queries/columns-with-log/{tableName}/{targetId}"}, method={RequestMethod.POST})
    public List<CoreAuditLogColumnDTO> selectColumnsByTargetInfo(@PathVariable(value="tableName") String var1, @PathVariable(value="targetId") String var2);

    @Log(value="\u67e5\u8be2\u5ba1\u8ba1\u8bb0\u5f55\u64cd\u4f5c\u8be6\u7ec6\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(value={"/activities/queries"}, method={RequestMethod.POST})
    public Page<CoreAuditLogActivityBean> selectActivityQueryMorePagination(RestJsonWrapperBean var1);
}
