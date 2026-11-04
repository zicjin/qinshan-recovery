/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogItemVerticalRepresentation;
import java.util.List;

public class CoreAuditLogVerticalRepresentation
extends AbstractBaseData {
    private static final long serialVersionUID = 6636718255195021936L;
    private String serviceName;
    private String lvl = "1";
    private List<CoreAuditLogItemVerticalRepresentation> children;

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

    public List<CoreAuditLogItemVerticalRepresentation> getChildren() {
        return this.children;
    }

    public void setChildren(List<CoreAuditLogItemVerticalRepresentation> children) {
        this.children = children;
    }
}
