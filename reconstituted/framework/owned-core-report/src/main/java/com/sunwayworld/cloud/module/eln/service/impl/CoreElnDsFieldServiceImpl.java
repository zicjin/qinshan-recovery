/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.service.impl;

import com.sunwayworld.cloud.module.eln.bean.CoreElnDsFieldBean;
import com.sunwayworld.cloud.module.eln.persistent.dao.CoreElnDsFieldDao;
import com.sunwayworld.cloud.module.eln.service.CoreElnDsFieldService;
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
public class CoreElnDsFieldServiceImpl
implements CoreElnDsFieldService {
    @Autowired
    private CoreElnDsFieldDao coreElnDsFieldDao;

    @Override
    public CoreElnDsFieldDao getDao() {
        return this.coreElnDsFieldDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        CoreElnDsFieldBean elnDsField = wrapper.parseUnique(CoreElnDsFieldBean.class);
        elnDsField.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(elnDsField);
        return elnDsField.getId();
    }
}
