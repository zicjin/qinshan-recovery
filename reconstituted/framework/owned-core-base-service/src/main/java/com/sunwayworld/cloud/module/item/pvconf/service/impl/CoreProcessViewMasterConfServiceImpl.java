/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pvconf.service.impl;

import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewMasterConfBean;
import com.sunwayworld.cloud.module.item.pvconf.persistent.dao.CoreProcessViewMasterConfDao;
import com.sunwayworld.cloud.module.item.pvconf.service.CoreProcessViewMasterConfService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreProcessViewMasterConfServiceImpl
implements CoreProcessViewMasterConfService {
    @Autowired
    protected CoreProcessViewMasterConfDao processViewMasterConfDao;

    @Override
    public CoreProcessViewMasterConfDao getDao() {
        return this.processViewMasterConfDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreProcessViewMasterConfBean masterConf = jsonWrapper.parseUnique(CoreProcessViewMasterConfBean.class);
        masterConf.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(masterConf);
        return masterConf.getId();
    }
}
