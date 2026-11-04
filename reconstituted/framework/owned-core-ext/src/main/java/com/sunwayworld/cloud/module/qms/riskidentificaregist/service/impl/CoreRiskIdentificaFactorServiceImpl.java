/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskidentificaregist.service.impl;

import com.sunwayworld.cloud.module.qms.riskidentificaregist.bean.CoreRiskIdentificaFactorBean;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.persistent.dao.CoreRiskIdentificaFactorDao;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.service.CoreRiskIdentificaFactorService;
import com.sunwayworld.cloud.module.qms.riskidentificaregist.service.CoreRiskIdentificaRegistService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreRiskIdentificaFactorServiceImpl
implements CoreRiskIdentificaFactorService {
    @Autowired
    private CoreRiskIdentificaFactorDao coreRiskIdentificaFactorDao;
    @Autowired
    @Lazy
    private CoreRiskIdentificaRegistService coreRiskIdentificaRegistService;

    @Override
    public CoreRiskIdentificaFactorDao getDao() {
        return this.coreRiskIdentificaFactorDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        List<CoreRiskIdentificaFactorBean> coreRiskIdentificaFactorList = wrapper.parse(CoreRiskIdentificaFactorBean.class);
        coreRiskIdentificaFactorList.forEach(t -> t.setId(ApplicationContextHelper.getNextIdentity()));
        this.getDao().insert(coreRiskIdentificaFactorList);
        return coreRiskIdentificaFactorList.get(0).getId();
    }
}
