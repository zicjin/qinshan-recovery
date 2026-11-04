/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.service.impl;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.context.TableContext;
import com.sunwayworld.framework.database.context.instance.TableContextInstance;
import com.sunwayworld.framework.exception.core.ApplicationRuntimeException;
import com.sunwayworld.framework.mybatis.service.MapDaoSupportService;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class MapDaoSupportServiceImpl
implements MapDaoSupportService {
    @Override
    public void preInsert(TableContextInstance instance) {
        this.preInsertList(instance.getContext(), Arrays.asList(instance));
    }

    @Override
    public void preInsertList(TableContext context, List<TableContextInstance> instanceList) {
        if (!LocalContextHelper.isUserLogin()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        instanceList.forEach(instance -> instance.getColumnInstanceList().stream().filter(c -> c.getValue() == null || c.getValue() instanceof String && StringUtils.isEmpty((String)c.getValue())).forEach(c -> {
            if ("createdById".equalsIgnoreCase(c.getColumnContext().getColumnName())) {
                c.setActive(true);
                c.setValue(LocalContextHelper.getLoginUserId());
                return;
            } else if ("createdByName".equalsIgnoreCase(c.getColumnContext().getColumnName())) {
                c.setActive(true);
                c.setValue(LocalContextHelper.getLoginUserName());
                return;
            } else if ("createdTime".equalsIgnoreCase(c.getColumnContext().getColumnName())) {
                c.setActive(true);
                c.setValue(now);
                return;
            } else if ("createdByOrgId".equalsIgnoreCase(c.getColumnContext().getColumnName())) {
                c.setActive(true);
                c.setValue(LocalContextHelper.getLoginOrgId());
                return;
            } else if ("createdByOrgName".equalsIgnoreCase(c.getColumnContext().getColumnName())) {
                c.setActive(true);
                c.setValue(LocalContextHelper.getLoginOrgName());
                return;
            } else if (c.getColumnContext().isNotNull()) {
                if (ObjectUtils.isEmpty(c.getColumnContext().getDefaultValue())) throw new ApplicationRuntimeException("MYBATIS.EXCEPTION.REQUIRE_NONE_NULL", context.getTableName() + "." + c.getColumnContext().getColumnName());
                c.setActive(true);
                c.setValue(c.getColumnContext().getDefaultValue());
                return;
            } else {
                c.setActive(true);
                c.setValue(null);
            }
        }));
    }
}
