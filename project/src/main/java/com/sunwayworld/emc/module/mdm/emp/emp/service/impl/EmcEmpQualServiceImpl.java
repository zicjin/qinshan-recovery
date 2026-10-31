package com.sunwayworld.emc.module.mdm.emp.emp.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpQualBean;
import com.sunwayworld.emc.module.mdm.emp.emp.dao.EmcEmpQualDao;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpQualService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author yuanhh@sunwayworld.com 2019年9月4日
 */
@Repository
public class EmcEmpQualServiceImpl implements EmcEmpQualService {

    @Autowired
    private EmcEmpQualDao emcEmpQualDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEmpQualDao getDao() {
        return emcEmpQualDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(String empId, RestJsonWrapperBean wrapper) {
        EmcEmpQualBean empQual = wrapper.parseUnique(EmcEmpQualBean.class);
        empQual.setId(ApplicationContextHelper.getNextIdentity());
        empQual.setEmpId(empId);
        getDao().insert(empQual);
        
        return empQual.getId();
    }
}
