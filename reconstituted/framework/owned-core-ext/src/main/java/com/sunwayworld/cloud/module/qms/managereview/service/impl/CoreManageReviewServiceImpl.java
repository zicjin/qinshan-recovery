/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.managereview.service.impl;

import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewBean;
import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewImproveBean;
import com.sunwayworld.cloud.module.qms.managereview.persistent.dao.CoreManageReviewDao;
import com.sunwayworld.cloud.module.qms.managereview.service.CoreManageReviewImproveService;
import com.sunwayworld.cloud.module.qms.managereview.service.CoreManageReviewService;
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
public class CoreManageReviewServiceImpl
implements CoreManageReviewService {
    @Autowired
    private CoreManageReviewDao coreManageReviewDao;
    @Autowired
    private CoreManageReviewImproveService coreManageReviewImproveService;

    @Override
    public CoreManageReviewDao getDao() {
        return this.coreManageReviewDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.INSERT")
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreManageReviewBean coreManageReview = jsonWrapper.parseUnique(CoreManageReviewBean.class);
        coreManageReview.setId(ApplicationContextHelper.getNextIdentity());
        coreManageReview.setReviewNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "GLPS"));
        this.getDao().insert(coreManageReview);
        return coreManageReview.getId();
    }

    @Override
    public Page<CoreManageReviewImproveBean> selectReviewImprovePaginationByManageReviewId(Long id, RestJsonWrapperBean wrapper) {
        return this.coreManageReviewImproveService.selectPaginationByFilter(SearchFilter.instance().match("MANAGEREVIEWID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.SAVE")
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    public void saveReviewImprove(Long id, RestJsonWrapperBean wrapper) {
        List<CoreManageReviewImproveBean> coreManageReviewImproveList = wrapper.parse(CoreManageReviewImproveBean.class);
        ArrayList insertCoreManageReviewImproveList = new ArrayList();
        ArrayList updateCoreManageReviewImproveList = new ArrayList();
        coreManageReviewImproveList.forEach(e -> {
            if (e.getId() == 0L) {
                e.setId(ApplicationContextHelper.getNextIdentity());
                e.setManageReviewId(id);
                insertCoreManageReviewImproveList.add(e);
            } else {
                updateCoreManageReviewImproveList.add(e);
            }
        });
        if (!insertCoreManageReviewImproveList.isEmpty()) {
            this.coreManageReviewImproveService.getDao().insert(insertCoreManageReviewImproveList);
        }
        if (!updateCoreManageReviewImproveList.isEmpty()) {
            this.coreManageReviewImproveService.getDao().update(updateCoreManageReviewImproveList, (String[])new String[0]);
        }
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.DELETE")
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    public void deleteReviewImprove(RestJsonWrapperBean wrapper) {
        this.coreManageReviewImproveService.delete(wrapper);
    }
}
