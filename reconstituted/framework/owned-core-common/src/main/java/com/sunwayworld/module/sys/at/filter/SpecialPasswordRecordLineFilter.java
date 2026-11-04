/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.filter;

import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineDTO;
import com.sunwayworld.module.sys.at.filter.SpecialRecordLineFilter;
import com.sunwayworld.module.sys.at.filter.SpecialRecordLineFilterContext;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SpecialPasswordRecordLineFilter
implements SpecialRecordLineFilter {
    public static final String SENSITIVE_DATA = "******";

    @Override
    public void filter(SpecialRecordLineFilterContext context) {
        List<CoreAuditTrailRecordLineDTO> itemList = context.getItemList();
        itemList.forEach(item -> {
            if ("password".equalsIgnoreCase(item.getColumn())) {
                item.setNewValue(SENSITIVE_DATA);
                item.setOldValue(SENSITIVE_DATA);
            }
        });
    }
}
