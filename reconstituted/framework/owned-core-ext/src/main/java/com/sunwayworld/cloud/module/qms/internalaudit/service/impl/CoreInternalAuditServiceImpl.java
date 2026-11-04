/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.internalaudit.service.impl;

import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditBean;
import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditGroupBean;
import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditNcrBean;
import com.sunwayworld.cloud.module.qms.internalaudit.persistent.dao.CoreInternalAuditDao;
import com.sunwayworld.cloud.module.qms.internalaudit.service.CoreInternalAuditGroupService;
import com.sunwayworld.cloud.module.qms.internalaudit.service.CoreInternalAuditNcrService;
import com.sunwayworld.cloud.module.qms.internalaudit.service.CoreInternalAuditService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreInternalAuditServiceImpl
implements CoreInternalAuditService {
    @Autowired
    private CoreInternalAuditDao coreInternalAuditDao;
    @Autowired
    private CoreInternalAuditGroupService coreInternalAuditGroupService;
    @Autowired
    private CoreInternalAuditNcrService coreInternalAuditNcrService;

    @Override
    public CoreInternalAuditDao getDao() {
        return this.coreInternalAuditDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreInternalAuditBean coreInternalAudit = jsonWrapper.parseUnique(CoreInternalAuditBean.class);
        coreInternalAudit.setId(ApplicationContextHelper.getNextIdentity());
        coreInternalAudit.setAuditNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "NBSH"));
        this.getDao().insert(coreInternalAudit);
        return coreInternalAudit.getId();
    }

    @Override
    public Page<CoreInternalAuditGroupBean> selectAuditGroupPaginationByInternalAuditId(Long id, RestJsonWrapperBean wrapper) {
        return this.coreInternalAuditGroupService.selectPaginationByFilter(SearchFilter.instance().match("AUDITID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.SAVE")
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    public void saveAuditGroup(Long id, RestJsonWrapperBean wrapper) {
        List<CoreInternalAuditGroupBean> coreInternalAuditGroupList = wrapper.parse(CoreInternalAuditGroupBean.class);
        ArrayList insertCoreInternalAuditGroupList = new ArrayList();
        ArrayList updateCoreInternalAuditGroupList = new ArrayList();
        coreInternalAuditGroupList.forEach(e -> {
            if (e.getId() == 0L) {
                e.setId(ApplicationContextHelper.getNextIdentity());
                e.setAuditId(id);
                insertCoreInternalAuditGroupList.add(e);
            } else {
                updateCoreInternalAuditGroupList.add(e);
            }
        });
        if (!insertCoreInternalAuditGroupList.isEmpty()) {
            this.coreInternalAuditGroupService.getDao().insert(insertCoreInternalAuditGroupList);
        }
        if (!updateCoreInternalAuditGroupList.isEmpty()) {
            this.coreInternalAuditGroupService.getDao().update(updateCoreInternalAuditGroupList, (String[])new String[0]);
        }
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.DELETE")
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    public void deleteAuditGroup(RestJsonWrapperBean wrapper) {
        this.coreInternalAuditGroupService.delete(wrapper);
    }

    @Override
    public Page<CoreInternalAuditNcrBean> selectAuditNcrPaginationByInternalAuditId(Long id, RestJsonWrapperBean wrapper) {
        return this.coreInternalAuditNcrService.selectPaginationByFilter(SearchFilter.instance().match("AUDITID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.SAVE")
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    public void saveAuditNcr(Long id, RestJsonWrapperBean wrapper) {
        List<CoreInternalAuditNcrBean> coreInternalAuditNcrList = wrapper.parse(CoreInternalAuditNcrBean.class);
        ArrayList insertCoreInternalAuditNcrList = new ArrayList();
        ArrayList updateCoreInternalAuditNcrList = new ArrayList();
        coreInternalAuditNcrList.forEach(e -> {
            if (e.getId() == 0L) {
                e.setId(ApplicationContextHelper.getNextIdentity());
                e.setAuditId(id);
                insertCoreInternalAuditNcrList.add(e);
            } else {
                updateCoreInternalAuditNcrList.add(e);
            }
        });
        if (!insertCoreInternalAuditNcrList.isEmpty()) {
            this.coreInternalAuditNcrService.getDao().insert(insertCoreInternalAuditNcrList);
        }
        if (!updateCoreInternalAuditNcrList.isEmpty()) {
            this.coreInternalAuditNcrService.getDao().update(updateCoreInternalAuditNcrList, (String[])new String[0]);
        }
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.DELETE")
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    public void deleteAuditNcr(RestJsonWrapperBean wrapper) {
        this.coreInternalAuditNcrService.delete(wrapper);
    }
}
