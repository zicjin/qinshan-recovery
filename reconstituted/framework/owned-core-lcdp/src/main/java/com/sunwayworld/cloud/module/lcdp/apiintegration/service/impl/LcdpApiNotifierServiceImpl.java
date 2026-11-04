/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.service.impl;

import com.sunwayworld.cloud.module.lcdp.apiintegration.bean.LcdpApiNotifierBean;
import com.sunwayworld.cloud.module.lcdp.apiintegration.persistent.dao.LcdpApiNotifierDao;
import com.sunwayworld.cloud.module.lcdp.apiintegration.service.LcdpApiNotifierService;
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

@Repository
@GikamBean
public class LcdpApiNotifierServiceImpl
implements LcdpApiNotifierService {
    @Autowired
    private LcdpApiNotifierDao lcdpApiNotifierDao;

    @Override
    public LcdpApiNotifierDao getDao() {
        return this.lcdpApiNotifierDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        List<LcdpApiNotifierBean> lcdpApiNotifierList = jsonWrapper.parse(LcdpApiNotifierBean.class);
        lcdpApiNotifierList.forEach(lcdpApiNotifier -> lcdpApiNotifier.setId(ApplicationContextHelper.getNextIdentity()));
        this.getDao().insert(lcdpApiNotifierList);
        return lcdpApiNotifierList.get(0).getId();
    }
}
