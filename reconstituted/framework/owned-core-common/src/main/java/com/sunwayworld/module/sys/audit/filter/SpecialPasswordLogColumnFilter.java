/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.filter;

import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnDTO;
import com.sunwayworld.module.sys.audit.filter.SpecialAuditLogColumnFilter;
import com.sunwayworld.module.sys.audit.filter.SpecialAuditLogColumnFilterContext;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SpecialPasswordLogColumnFilter
implements SpecialAuditLogColumnFilter {
    public static final String SENSITIVE_DATA = "******";

    @Override
    public void filter(SpecialAuditLogColumnFilterContext context) {
        List<CoreAuditLogColumnDTO> itemList = context.getItemList();
        itemList.forEach(item -> {
            if ("password".equalsIgnoreCase(item.getColumn())) {
                item.setNewValue(SENSITIVE_DATA);
                item.setOldValue(SENSITIVE_DATA);
            }
        });
    }
}
