package com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.dao.EmcQcCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@GikamBean
@Repository
public class EmcQcCategoryServiceImpl implements EmcQcCategoryService {

    @Autowired
    private EmcQcCategoryDao qcCategoryDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcQcCategoryDao getDao() {
        return qcCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcQcCategoryBean qcCategory = wrapper.parseUnique(EmcQcCategoryBean.class);
        qcCategory.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(qcCategory);

        return qcCategory.getId();
    }

}