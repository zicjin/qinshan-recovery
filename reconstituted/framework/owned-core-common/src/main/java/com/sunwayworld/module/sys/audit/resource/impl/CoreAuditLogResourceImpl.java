/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
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
import com.sunwayworld.module.sys.audit.resource.CoreAuditLogResource;
import com.sunwayworld.module.sys.audit.service.CoreAuditLogService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u65b0\u7248\u5ba1\u8ba1\u8ddf\u8e2a\u65e5\u5fd7")
@GikamBean
@RestController
public class CoreAuditLogResourceImpl
implements CoreAuditLogResource {
    @Autowired
    private CoreAuditLogService auditLogService;

    @Override
    public CoreAuditLogDTO selectAuditLogRoot(@RequestParam String table, @RequestParam String id) {
        return this.auditLogService.selectAuditLogRoot(table, id);
    }

    @Override
    public List<CoreAuditLogItemDTO> selectAuditLogItemList(@RequestParam(required=false) String vertical, @RequestParam String table, @RequestParam(required=false) Long auditLogId, @RequestParam(required=false) Long parentAuditLogId) {
        return this.auditLogService.selectAuditLogItemList(vertical, table, auditLogId, parentAuditLogId);
    }

    @Override
    public CoreAuditLogItemVerticalDTO selectAuditLogItemActivityVertical(@PathVariable Long auditLogId) {
        return this.auditLogService.selectAuditLogItemActivityVertical(auditLogId);
    }

    @Override
    public List<CoreAuditLogColumnVerticalDTO> selectAuditLogItemActivityColumnVerticalList(@PathVariable Long activityId) {
        return this.auditLogService.selectAuditLogItemActivityColumnVerticalList(activityId);
    }

    @Override
    public CoreAuditLogItemHorizontalDTO selectAuditLogItemActivityHorizontal(@PathVariable Long auditLogId) {
        return this.auditLogService.selectAuditLogItemActivityHorizontal(auditLogId);
    }

    @Override
    public CoreAuditLogVerticalRepresentation selectAuditLogVertical(@RequestParam String table, @RequestParam String id) {
        return this.auditLogService.selectAuditLogVertical(table, id);
    }

    @Override
    public CoreAuditLogHorizontalRepresentation selectAuditLogHorizontal(@RequestParam String table, @RequestParam String id) {
        return this.auditLogService.selectAuditLogHorizontal(table, id);
    }

    @Override
    public Page<CoreAuditLogTraceOverviewDTO> selectTraceOverview(RestJsonWrapperBean wrapper) {
        return this.auditLogService.selectTraceOverview(wrapper);
    }

    @Override
    public List<CoreAuditLogColumnDTO> selectColumnsByTargetInfo(String tableName, String targetId) {
        return this.auditLogService.selectColumnsByTargetInfo(tableName, targetId);
    }

    @Override
    public Page<CoreAuditLogActivityBean> selectActivityQueryMorePagination(RestJsonWrapperBean wrapper) {
        return this.auditLogService.selectActivityQueryMorePagination(wrapper);
    }
}
