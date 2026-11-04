/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.externalaudit.service.impl;

import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditBean;
import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditGroupBean;
import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditNcrBean;
import com.sunwayworld.cloud.module.qms.externalaudit.persistent.dao.CoreExternalAuditDao;
import com.sunwayworld.cloud.module.qms.externalaudit.service.CoreExternalAuditGroupService;
import com.sunwayworld.cloud.module.qms.externalaudit.service.CoreExternalAuditNcrService;
import com.sunwayworld.cloud.module.qms.externalaudit.service.CoreExternalAuditService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreExternalAuditServiceImpl
implements CoreExternalAuditService {
    @Autowired
    private CoreExternalAuditDao coreExternalAuditDao;
    @Autowired
    private CoreExternalAuditGroupService coreExternalAuditGroupService;
    @Autowired
    private CoreExternalAuditNcrService coreExternalAuditNcrService;

    @Override
    public CoreExternalAuditDao getDao() {
        return this.coreExternalAuditDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreExternalAuditBean coreExternalAudit = jsonWrapper.parseUnique(CoreExternalAuditBean.class);
        coreExternalAudit.setId(ApplicationContextHelper.getNextIdentity());
        coreExternalAudit.setAuditNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "WBSH"));
        coreExternalAudit.setAuditOrgName(LocalContextHelper.getLoginOrgName());
        this.getDao().insert(coreExternalAudit);
        return coreExternalAudit.getId();
    }

    @Override
    public Page<CoreExternalAuditGroupBean> selectAuditGroupPaginationByExternalAuditId(Long id, RestJsonWrapperBean wrapper) {
        return this.coreExternalAuditGroupService.selectPaginationByFilter(SearchFilter.instance().match("AUDITID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insertAuditGroup(Long id, RestJsonWrapperBean wrapper) {
        List<CoreExternalAuditGroupBean> coreExternalAuditGroupList = wrapper.parse(CoreExternalAuditGroupBean.class);
        coreExternalAuditGroupList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setAuditId(id);
        });
        this.coreExternalAuditGroupService.getDao().insert(coreExternalAuditGroupList);
        return coreExternalAuditGroupList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteAuditGroup(Long id, RestJsonWrapperBean wrapper) {
        this.coreExternalAuditGroupService.delete(wrapper);
    }

    @Override
    public Page<CoreExternalAuditNcrBean> selectAuditNcrPaginationByExternalAuditId(Long id, RestJsonWrapperBean wrapper) {
        return this.coreExternalAuditNcrService.selectPaginationByFilter(SearchFilter.instance().match("AUDITID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insertAuditNcr(Long id, RestJsonWrapperBean wrapper) {
        List<CoreExternalAuditNcrBean> coreExternalAuditNcrList = wrapper.parse(CoreExternalAuditNcrBean.class);
        coreExternalAuditNcrList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setAuditId(id);
        });
        this.coreExternalAuditNcrService.getDao().insert(coreExternalAuditNcrList);
        return coreExternalAuditNcrList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteAuditNcr(Long id, RestJsonWrapperBean wrapper) {
        this.coreExternalAuditNcrService.delete(wrapper);
    }
}
