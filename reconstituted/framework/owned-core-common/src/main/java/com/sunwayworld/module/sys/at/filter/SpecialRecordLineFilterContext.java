/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.filter;

import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineDTO;
import java.util.List;

public class SpecialRecordLineFilterContext {
    private final List<CoreAuditTrailRecordLineDTO> itemList;

    public SpecialRecordLineFilterContext(List<CoreAuditTrailRecordLineDTO> itemList) {
        this.itemList = itemList;
    }

    public List<CoreAuditTrailRecordLineDTO> getItemList() {
        return this.itemList;
    }
}
