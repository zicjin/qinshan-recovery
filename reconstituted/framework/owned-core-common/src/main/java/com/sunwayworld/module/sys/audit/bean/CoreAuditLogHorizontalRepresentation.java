/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemHorizontalRepresentation;
import java.util.List;

public class CoreAuditLogHorizontalRepresentation
extends AbstractBaseData {
    private static final long serialVersionUID = 6636718255195021936L;
    private String serviceName;
    private String lvl = "1";
    private List<CoreAuditLogItemHorizontalRepresentation> children;

    public String getServiceName() {
        return this.serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getLvl() {
        return this.lvl;
    }

    public void setLvl(String lvl) {
        this.lvl = lvl;
    }

    public List<CoreAuditLogItemHorizontalRepresentation> getChildren() {
        return this.children;
    }

    public void setChildren(List<CoreAuditLogItemHorizontalRepresentation> children) {
        this.children = children;
    }
}
