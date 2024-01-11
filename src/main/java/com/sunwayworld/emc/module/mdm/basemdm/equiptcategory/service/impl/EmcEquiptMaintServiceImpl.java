package com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.dao.EmcEquiptMaintDao;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptMaintService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magh@sunwayworld.com 2019年9月4日
 */
@Repository
public class EmcEquiptMaintServiceImpl implements EmcEquiptMaintService {

    @Autowired
    private EmcEquiptMaintDao emcEquiptMaintDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcEquiptMaintDao getDao() {
        return emcEquiptMaintDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long equiptCategoryId, RestJsonWrapperBean wrapper) {
        EmcEquiptMaintBean emcEquiptMaintBean = wrapper.parseUnique(EmcEquiptMaintBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcEquiptMaintBean.setId(id);
        emcEquiptMaintBean.setEquiptCategoryId(equiptCategoryId);
        getDao().insert(emcEquiptMaintBean);
        
        return id;
    }
}
