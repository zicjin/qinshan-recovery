/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnNameDTO;
import com.sunwayworld.module.sys.audit.dao.CoreAuditLogColumnDao;
import com.sunwayworld.module.sys.audit.service.CoreAuditLogColumnService;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAuditLogColumnServiceImpl
implements CoreAuditLogColumnService {
    @Autowired
    private CoreAuditLogColumnDao logColumnDao;

    @Override
    public CoreAuditLogColumnDao getDao() {
        return this.logColumnDao;
    }

    @Override
    public List<CoreAuditLogColumnNameDTO> selectDeletedNames(List<String> targetIds, String tableName, String nameField) {
        if (StringUtils.isBlank(nameField) || StringUtils.isBlank(tableName) || CollectionUtils.isEmpty(targetIds)) {
            return Collections.emptyList();
        }
        return this.getDao().selectDeletedNames(targetIds, tableName, nameField);
    }

    @Override
    public List<CoreAuditLogColumnDTO> selectColumnsByTargetInfo(String tableName, String id) {
        if (StringUtils.isBlank(tableName) || StringUtils.isBlank(id)) {
            return Collections.emptyList();
        }
        return this.getDao().selectColumnsByTargetInfo(tableName, id);
    }
}
