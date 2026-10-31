package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryProjectBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.dao.EmcBizCategoryProjectDao;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryProjectService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcBizCategoryProjectServiceImpl implements EmcBizCategoryProjectService {

    @Autowired
    private EmcBizCategoryProjectDao emcBizCategoryProjectDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcBizCategoryProjectDao getDao() {
        return emcBizCategoryProjectDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcBizCategoryProjectBean emcBizCategoryProject = jsonWrapper.parseUnique(EmcBizCategoryProjectBean.class);
        emcBizCategoryProject.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcBizCategoryProject);
        return emcBizCategoryProject.getId();
    }

    @Override
    public void insert(Long bizCategoryId, RestJsonWrapperBean wrapper) {
        EmcBizCategoryProjectBean emcBizCategoryProject = wrapper.parseUnique(EmcBizCategoryProjectBean.class);
        emcBizCategoryProject.setId(ApplicationContextHelper.getNextIdentity());
        emcBizCategoryProject.setBizCategoryId(bizCategoryId);
        getDao().insert(emcBizCategoryProject);
    }

}
