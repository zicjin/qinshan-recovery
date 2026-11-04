/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.service.impl;

import com.sunwayworld.cloud.module.les.bean.CoreLesTemplateOrgBean;
import com.sunwayworld.cloud.module.les.persistent.dao.CoreLesTemplateOrgDao;
import com.sunwayworld.cloud.module.les.service.CoreLesTemplateOrgService;
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
public class CoreLesTemplateOrgServiceImpl
implements CoreLesTemplateOrgService {
    @Autowired
    private CoreLesTemplateOrgDao coreLesTemplateOrgDao;

    @Override
    public CoreLesTemplateOrgDao getDao() {
        return this.coreLesTemplateOrgDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        CoreLesTemplateOrgBean templateOrg = wrapper.parseUnique(CoreLesTemplateOrgBean.class);
        templateOrg.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(templateOrg);
        return templateOrg.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void insertTemplateOrg(RestJsonWrapperBean wrapper) {
        List<CoreLesTemplateOrgBean> templateOrgList = wrapper.parse(CoreLesTemplateOrgBean.class);
        templateOrgList.forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
        this.getDao().insert(templateOrgList);
    }
}
