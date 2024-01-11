package com.sunwayworld.emc.module.mdm.org.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.org.bean.EmcOrgPermissionBean;
import com.sunwayworld.emc.module.mdm.org.dao.EmcOrgPermissionDao;
import com.sunwayworld.emc.module.mdm.org.service.EmcOrgPermissionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
public class EmcOrgPermissionServiceImpl implements EmcOrgPermissionService {

    @Autowired
    private EmcOrgPermissionDao emcOrgPermissionDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcOrgPermissionDao getDao() {
        return emcOrgPermissionDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        List<EmcOrgPermissionBean> emcOrgPermissionBeanList = wrapper.parse(EmcOrgPermissionBean.class);
        emcOrgPermissionBeanList.stream().forEach(e -> e.setId(ApplicationContextHelper.getNextIdentity()));
        
        getDao().insert(emcOrgPermissionBeanList);
        return emcOrgPermissionBeanList.get(0).getId();
    }

}
