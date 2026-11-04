/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.bean;

import com.sunwayworld.framework.support.domain.AbstractBaseData;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnValueHorizontalDTO;
import java.util.List;

public class CoreAuditLogItemActivityHorizontalDTO
extends AbstractBaseData {
    private static final long serialVersionUID = -8986246442618714723L;
    private List<CoreAuditLogColumnValueHorizontalDTO> valueList;
    private String lvl = "3";

    public String getLvl() {
        return this.lvl;
    }

    public void setLvl(String lvl) {
        this.lvl = lvl;
    }

    public List<CoreAuditLogColumnValueHorizontalDTO> getValueList() {
        return this.valueList;
    }

    public void setValueList(List<CoreAuditLogColumnValueHorizontalDTO> valueList) {
        this.valueList = valueList;
    }
}
