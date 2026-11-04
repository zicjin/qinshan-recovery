/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemActivityVerticalDTO;
import java.util.List;

public class CoreAuditLogItemVerticalDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -858355096778573494L;
    private List<CoreAuditLogItemActivityVerticalDTO> activityList;
    private List<CoreAuditLogDTO> childAuditLogList;

    public List<CoreAuditLogItemActivityVerticalDTO> getActivityList() {
        return this.activityList;
    }

    public void setActivityList(List<CoreAuditLogItemActivityVerticalDTO> activityList) {
        this.activityList = activityList;
    }

    public List<CoreAuditLogDTO> getChildAuditLogList() {
        return this.childAuditLogList;
    }

    public void setChildAuditLogList(List<CoreAuditLogDTO> childAuditLogList) {
        this.childAuditLogList = childAuditLogList;
    }
}
