/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.filter;

import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnDTO;
import java.util.List;

public class SpecialAuditLogColumnFilterContext {
    private final List<CoreAuditLogColumnDTO> itemList;

    public SpecialAuditLogColumnFilterContext(List<CoreAuditLogColumnDTO> itemList) {
        this.itemList = itemList;
    }

    public List<CoreAuditLogColumnDTO> getItemList() {
        return this.itemList;
    }
}
