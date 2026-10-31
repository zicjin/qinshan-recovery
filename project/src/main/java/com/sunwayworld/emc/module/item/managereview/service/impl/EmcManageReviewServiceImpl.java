package com.sunwayworld.emc.module.item.managereview.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.managereview.bean.EmcManageReviewBean;
import com.sunwayworld.emc.module.item.managereview.dao.EmcManageReviewDao;
import com.sunwayworld.emc.module.item.managereview.service.EmcManageReviewService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
public class EmcManageReviewServiceImpl implements EmcManageReviewService {
    @Autowired
    private EmcManageReviewDao emcManageReviewDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcManageReviewDao getDao() {
        return emcManageReviewDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcManageReviewBean emcManageReview = jsonWrapper.parseUnique(EmcManageReviewBean.class);
        emcManageReview.setId(ApplicationContextHelper.getNextIdentity());
        emcManageReview.setQualityControlNo(ApplicationContextHelper.getNextSequence(getDao().getTable(),"GLPS"));
        getDao().insert(emcManageReview);
        return emcManageReview.getId();
    }

}
