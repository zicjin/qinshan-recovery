/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnHeaderHorizontalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemActivityHorizontalDTO;
import java.util.List;

public class CoreAuditLogItemHorizontalDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -858355096778573494L;
    private List<CoreAuditLogColumnHeaderHorizontalDTO> headerList;
    private List<CoreAuditLogItemActivityHorizontalDTO> activityList;
    private List<CoreAuditLogDTO> childAuditLogList;

    public List<CoreAuditLogColumnHeaderHorizontalDTO> getHeaderList() {
        return this.headerList;
    }

    public void setHeaderList(List<CoreAuditLogColumnHeaderHorizontalDTO> headerList) {
        this.headerList = headerList;
    }

    public List<CoreAuditLogItemActivityHorizontalDTO> getActivityList() {
        return this.activityList;
    }

    public void setActivityList(List<CoreAuditLogItemActivityHorizontalDTO> activityList) {
        this.activityList = activityList;
    }

    public List<CoreAuditLogDTO> getChildAuditLogList() {
        return this.childAuditLogList;
    }

    public void setChildAuditLogList(List<CoreAuditLogDTO> childAuditLogList) {
        this.childAuditLogList = childAuditLogList;
    }
}
