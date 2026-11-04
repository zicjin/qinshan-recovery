/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.service.impl;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigColumnBean;
import com.sunwayworld.module.sys.audit.dao.CoreAuditConfigColumnDao;
import com.sunwayworld.module.sys.audit.service.CoreAuditConfigColumnService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAuditConfigColumnServiceImpl
implements CoreAuditConfigColumnService {
    @Autowired
    private CoreAuditConfigColumnDao configColumnDao;

    @Override
    public CoreAuditConfigColumnDao getDao() {
        return this.configColumnDao;
    }

    @Override
    @Cacheable(value={"T_CORE_AUDIT_CONFIG_COLUMN_LIST.BY_CONFIGID"}, key="#configId")
    public List<CoreAuditConfigColumnBean> selectListByConfigId(String configId) {
        CoreAuditConfigColumnBean filter = new CoreAuditConfigColumnBean();
        filter.setConfigId(configId);
        return this.getDao().selectList(filter, new Order[0]);
    }

    @Override
    @Cacheable(value={"T_CORE_AUDIT_CONFIG_COLUMN_AUDITABLE_LIST.BY_CONFIGID"}, key="#configId")
    public List<CoreAuditConfigColumnBean> selectAuditableListByConfigId(String configId) {
        CoreAuditConfigColumnBean filter = new CoreAuditConfigColumnBean();
        filter.setConfigId(configId);
        filter.setAuditable("1");
        return this.getDao().selectList(filter, new Order[0]);
    }
}
