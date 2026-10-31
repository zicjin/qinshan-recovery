package com.sunwayworld.emc.module.mdm.basemdm.testcategory.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.testcategory.bean.EmcTestCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.dao.EmcTestCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.testcategory.service.EmcTestCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
public class EmcTestCategoryServiceImpl implements EmcTestCategoryService {

    @Autowired
    private EmcTestCategoryDao emcTestCategoryDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcTestCategoryDao getDao() {
        return emcTestCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcTestCategoryBean emcTestCategoryBean = wrapper.parseUnique(EmcTestCategoryBean.class);
        emcTestCategoryBean.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcTestCategoryBean);
        return emcTestCategoryBean.getId();
    }

}