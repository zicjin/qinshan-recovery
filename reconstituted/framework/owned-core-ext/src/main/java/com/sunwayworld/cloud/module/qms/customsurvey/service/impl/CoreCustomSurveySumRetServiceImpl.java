/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsurvey.service.impl;

import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveySumRetBean;
import com.sunwayworld.cloud.module.qms.customsurvey.persistent.dao.CoreCustomSurveySumRetDao;
import com.sunwayworld.cloud.module.qms.customsurvey.service.CoreCustomSurveySumRetService;
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
public class CoreCustomSurveySumRetServiceImpl
implements CoreCustomSurveySumRetService {
    @Autowired
    private CoreCustomSurveySumRetDao coreCustomSurveySumRetDao;

    @Override
    public CoreCustomSurveySumRetDao getDao() {
        return this.coreCustomSurveySumRetDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreCustomSurveySumRetBean coreCustomSurveySumRet = jsonWrapper.parseUnique(CoreCustomSurveySumRetBean.class);
        coreCustomSurveySumRet.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreCustomSurveySumRet);
        return coreCustomSurveySumRet.getId();
    }
}
