/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.datacenter.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.datacenter.bean.CoreDataCenterParamBean;
import com.sunwayworld.module.item.datacenter.dao.CoreDataCenterParamDao;
import com.sunwayworld.module.item.datacenter.service.CoreDataCenterParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreDataCenterParamServiceImpl
implements CoreDataCenterParamService {
    @Autowired
    private CoreDataCenterParamDao coreDataCenterParamDao;

    @Override
    public CoreDataCenterParamDao getDao() {
        return this.coreDataCenterParamDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreDataCenterParamBean coreDataCenterParam = jsonWrapper.parseUnique(CoreDataCenterParamBean.class);
        coreDataCenterParam.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreDataCenterParam);
        return coreDataCenterParam.getId();
    }
}
