package com.sunwayworld.emc.module.mdm.contract.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcReviewContentBean;
import com.sunwayworld.emc.module.mdm.contract.dao.EmcReviewContentDao;
import com.sunwayworld.emc.module.mdm.contract.service.EmcReviewContentService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcReviewContentServiceImpl implements EmcReviewContentService {

    @Autowired
    private EmcReviewContentDao emcReviewContentDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcReviewContentDao getDao() {
        return emcReviewContentDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcReviewContentBean emcReviewContent = jsonWrapper.parseUnique(EmcReviewContentBean.class);
        emcReviewContent.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcReviewContent);
        return emcReviewContent.getId();
    }

}
