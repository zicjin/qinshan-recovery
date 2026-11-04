/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pvconf.service.impl;

import com.sunwayworld.cloud.module.item.pvconf.bean.CoreProcessViewSubConfBean;
import com.sunwayworld.cloud.module.item.pvconf.persistent.dao.CoreProcessViewSubConfDao;
import com.sunwayworld.cloud.module.item.pvconf.service.CoreProcessViewSubConfService;
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
public class CoreProcessViewSubConfServiceImpl
implements CoreProcessViewSubConfService {
    @Autowired
    protected CoreProcessViewSubConfDao processViewSubConfDao;

    @Override
    public CoreProcessViewSubConfDao getDao() {
        return this.processViewSubConfDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreProcessViewSubConfBean subConf = jsonWrapper.parseUnique(CoreProcessViewSubConfBean.class);
        subConf.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(subConf);
        return subConf.getId();
    }
}
