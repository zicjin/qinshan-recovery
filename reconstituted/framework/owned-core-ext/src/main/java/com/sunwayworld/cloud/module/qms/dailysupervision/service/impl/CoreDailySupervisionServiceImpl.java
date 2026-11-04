/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.dailysupervision.service.impl;

import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionConBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionItemBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.persistent.dao.CoreDailySupervisionDao;
import com.sunwayworld.cloud.module.qms.dailysupervision.service.CoreDailySupervisionConService;
import com.sunwayworld.cloud.module.qms.dailysupervision.service.CoreDailySupervisionItemService;
import com.sunwayworld.cloud.module.qms.dailysupervision.service.CoreDailySupervisionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreDailySupervisionServiceImpl
implements CoreDailySupervisionService {
    @Autowired
    private CoreDailySupervisionDao coreDailySupervisionDao;
    @Autowired
    private CoreDailySupervisionItemService coreDailySupervisionItemService;
    @Autowired
    private CoreDailySupervisionConService coreDailySupervisionConService;

    @Override
    public CoreDailySupervisionDao getDao() {
        return this.coreDailySupervisionDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreDailySupervisionBean coreDailySupervision = new CoreDailySupervisionBean();
        coreDailySupervision.setId(ApplicationContextHelper.getNextIdentity());
        coreDailySupervision.setDailyNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "RCJD"));
        this.getDao().insert(coreDailySupervision);
        CoreDailySupervisionItemBean coreDailySupervisionItem = jsonWrapper.parseUnique(CoreDailySupervisionItemBean.class);
        coreDailySupervisionItem.setId(ApplicationContextHelper.getNextIdentity());
        coreDailySupervisionItem.setDailySupId(coreDailySupervision.getId());
        this.coreDailySupervisionItemService.getDao().insert((CoreDailySupervisionItemBean)coreDailySupervisionItem);
        return coreDailySupervision.getId();
    }

    @Override
    public Page<CoreDailySupervisionItemBean> selectSupervisionItemPaginationByDailySupervisionId(Long id, RestJsonWrapperBean wrapper) {
        return this.coreDailySupervisionItemService.selectPaginationByFilter(SearchFilter.instance().match("DAILYSUPID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insertSupervisionItem(Long id, RestJsonWrapperBean wrapper) {
        CoreDailySupervisionItemBean coreDailySupervisionItem = wrapper.parseUnique(CoreDailySupervisionItemBean.class);
        coreDailySupervisionItem.setId(ApplicationContextHelper.getNextIdentity());
        coreDailySupervisionItem.setDailySupId(id);
        this.coreDailySupervisionItemService.getDao().insert((CoreDailySupervisionItemBean)coreDailySupervisionItem);
        return coreDailySupervisionItem.getId();
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.DELETE")
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    public void deleteSupervisionItem(Long id, RestJsonWrapperBean wrapper) {
        this.coreDailySupervisionItemService.delete(wrapper);
    }

    @Override
    public Page<CoreDailySupervisionConBean> selectSupervisionConPaginationByDailySupervisionId(Long id, RestJsonWrapperBean wrapper) {
        return this.coreDailySupervisionConService.selectPaginationByFilter(SearchFilter.instance().match("DAILYSUPITEMID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insertSupervisionCon(Long id, RestJsonWrapperBean wrapper) {
        CoreDailySupervisionItemBean coreDailySupervisionItem = (CoreDailySupervisionItemBean)this.coreDailySupervisionItemService.selectById(id);
        CoreDailySupervisionConBean coreDailySupervisionCon = wrapper.parseUnique(CoreDailySupervisionConBean.class);
        coreDailySupervisionCon.setId(ApplicationContextHelper.getNextIdentity());
        coreDailySupervisionCon.setDailySupItemId(id);
        coreDailySupervisionCon.setAuditName(coreDailySupervisionItem.getAuditName());
        this.coreDailySupervisionConService.getDao().insert((CoreDailySupervisionConBean)coreDailySupervisionCon);
        return coreDailySupervisionCon.getId();
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.DELETE")
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    public void deleteSupervisionCon(Long id, RestJsonWrapperBean wrapper) {
        this.coreDailySupervisionConService.delete(wrapper);
    }
}
