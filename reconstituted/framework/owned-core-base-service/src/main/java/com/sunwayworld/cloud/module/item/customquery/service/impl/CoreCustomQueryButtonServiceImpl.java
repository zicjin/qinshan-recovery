/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customquery.service.impl;

import com.sunwayworld.cloud.module.item.customquery.bean.CoreCustomQueryButtonBean;
import com.sunwayworld.cloud.module.item.customquery.presistent.dao.CoreCustomQueryButtonDao;
import com.sunwayworld.cloud.module.item.customquery.service.CoreCustomQueryButtonService;
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
public class CoreCustomQueryButtonServiceImpl
implements CoreCustomQueryButtonService {
    @Autowired
    private CoreCustomQueryButtonDao coreCustomQueryButtonDao;

    @Override
    public CoreCustomQueryButtonDao getDao() {
        return this.coreCustomQueryButtonDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreCustomQueryButtonBean coreCustomQueryButton = jsonWrapper.parseUnique(CoreCustomQueryButtonBean.class);
        coreCustomQueryButton.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreCustomQueryButton);
        return coreCustomQueryButton.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void addQueryButtons(RestJsonWrapperBean wrapper) {
        List<CoreCustomQueryButtonBean> queryButtonList = wrapper.parse(CoreCustomQueryButtonBean.class);
        queryButtonList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setOrderNo(e.getId());
        });
        this.getDao().insert(queryButtonList);
    }
}
