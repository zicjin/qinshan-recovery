/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemActivityVerticalRepresentation;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogVerticalRepresentation;
import java.util.List;

public class CoreAuditLogItemVerticalRepresentation
extends AbstractBaseData {
    private static final long serialVersionUID = -6425000537673594190L;
    private String itemDesc;
    private String lvl = "2";
    private List<CoreAuditLogItemActivityVerticalRepresentation> activityList;
    private List<CoreAuditLogVerticalRepresentation> children;

    public String getItemDesc() {
        return this.itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getLvl() {
        return this.lvl;
    }

    public void setLvl(String lvl) {
        this.lvl = lvl;
    }

    public List<CoreAuditLogItemActivityVerticalRepresentation> getActivityList() {
        return this.activityList;
    }

    public void setActivityList(List<CoreAuditLogItemActivityVerticalRepresentation> activityList) {
        this.activityList = activityList;
    }

    public List<CoreAuditLogVerticalRepresentation> getChildren() {
        return this.children;
    }

    public void setChildren(List<CoreAuditLogVerticalRepresentation> children) {
        this.children = children;
    }
}
