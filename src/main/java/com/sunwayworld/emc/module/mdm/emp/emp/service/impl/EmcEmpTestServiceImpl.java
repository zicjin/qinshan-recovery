package com.sunwayworld.emc.module.mdm.emp.emp.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTestBean;
import com.sunwayworld.emc.module.mdm.emp.emp.dao.EmcEmpTestDao;
import com.sunwayworld.emc.module.mdm.emp.emp.service.EmcEmpTestService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author yuanhh@sunwayworld.com 2019年9月4日
 */
@Repository
public class EmcEmpTestServiceImpl implements EmcEmpTestService {

    @Autowired
    private EmcEmpTestDao emcEmpTestDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEmpTestDao getDao() {
        return emcEmpTestDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(String empId, RestJsonWrapperBean wrapper) {
        List<EmcEmpTestBean> empTestList = wrapper.parse(EmcEmpTestBean.class);
        
        empTestList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setEmpId(empId);
        });
        
        getDao().insert(empTestList);
    }
}
