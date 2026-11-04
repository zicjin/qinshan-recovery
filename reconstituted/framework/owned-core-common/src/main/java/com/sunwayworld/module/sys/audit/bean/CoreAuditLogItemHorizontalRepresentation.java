/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnHeaderHorizontalDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogHorizontalRepresentation;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemActivityHorizontalDTO;
import java.util.List;

public class CoreAuditLogItemHorizontalRepresentation
extends AbstractBaseData {
    private static final long serialVersionUID = -858355096778573494L;
    private String itemDesc;
    private String lvl = "2";
    private List<CoreAuditLogColumnHeaderHorizontalDTO> headerList;
    private List<CoreAuditLogItemActivityHorizontalDTO> activityList;
    private List<CoreAuditLogHorizontalRepresentation> children;

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

    public List<CoreAuditLogHorizontalRepresentation> getChildren() {
        return this.children;
    }

    public void setChildren(List<CoreAuditLogHorizontalRepresentation> children) {
        this.children = children;
    }

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
}
