/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.pagefield.service.impl;

import com.sunwayworld.cloud.module.item.pagefield.bean.CorePageFieldCompBean;
import com.sunwayworld.cloud.module.item.pagefield.persistent.dao.CorePageFieldCompDao;
import com.sunwayworld.cloud.module.item.pagefield.service.CorePageFieldCompService;
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
public class CorePageFieldCompServiceImpl
implements CorePageFieldCompService {
    @Autowired
    protected CorePageFieldCompDao corePageFieldCompDao;

    @Override
    public CorePageFieldCompDao getDao() {
        return this.corePageFieldCompDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(Long pfId, RestJsonWrapperBean jsonWrapper) {
        List<CorePageFieldCompBean> compList = jsonWrapper.parse(CorePageFieldCompBean.class);
        compList.forEach(c -> {
            c.setId(ApplicationContextHelper.getNextIdentity());
            c.setPfId(pfId);
        });
        this.getDao().insert(compList);
        return compList.get(0).getId();
    }
}
