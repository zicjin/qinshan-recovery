/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pagefield.service.impl;

import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompFieldBean;
import com.sunwayworld.cloud.module.item.pagefield.persistent.dao.CorePageFieldCompFieldDao;
import com.sunwayworld.cloud.module.item.pagefield.service.CorePageFieldCompFieldService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CorePageFieldCompFieldServiceImpl
implements CorePageFieldCompFieldService {
    @Autowired
    protected CorePageFieldCompFieldDao corePageFieldCompFieldDao;

    @Override
    public CorePageFieldCompFieldDao getDao() {
        return this.corePageFieldCompFieldDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(Long pfcId, RestJsonWrapperBean jsonWrapper) {
        List<CorePageFieldCompFieldBean> compFieldList = jsonWrapper.parse(CorePageFieldCompFieldBean.class);
        compFieldList.forEach(cf -> {
            cf.setId(ApplicationContextHelper.getNextIdentity());
            cf.setPfcId(pfcId);
        });
        this.getDao().insert(compFieldList);
        return compFieldList.get(0).getId();
    }
}
