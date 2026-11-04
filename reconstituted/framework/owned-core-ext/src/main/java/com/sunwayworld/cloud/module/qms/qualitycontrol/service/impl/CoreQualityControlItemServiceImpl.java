/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.qualitycontrol.service.impl;

import com.sunwayworld.cloud.module.qms.qualitycontrol.bean.CoreQualityControlItemBean;
import com.sunwayworld.cloud.module.qms.qualitycontrol.persistent.dao.CoreQualityControlItemDao;
import com.sunwayworld.cloud.module.qms.qualitycontrol.service.CoreQualityControlItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreQualityControlItemServiceImpl
implements CoreQualityControlItemService {
    @Autowired
    private CoreQualityControlItemDao coreQualityControlItemDao;

    @Override
    public CoreQualityControlItemDao getDao() {
        return this.coreQualityControlItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreQualityControlItemBean coreQualityControlItem = jsonWrapper.parseUnique(CoreQualityControlItemBean.class);
        coreQualityControlItem.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreQualityControlItem);
        return coreQualityControlItem.getId();
    }
}
