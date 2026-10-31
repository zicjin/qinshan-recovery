package com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcOrderDescBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.dao.EmcOrderDescDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.service.EmcOrderDescService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magh@sunwayworld.com 2019年9月6日
 */

@Repository
public class EmcOrderDescServiceImpl implements EmcOrderDescService {
    @Autowired
    private EmcOrderDescDao emcOrderDescDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcOrderDescDao getDao() {
        return emcOrderDescDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long monitorTmplGroupId, RestJsonWrapperBean wrapper) {
        EmcOrderDescBean emcOrderDescBean = wrapper.parseUnique(EmcOrderDescBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcOrderDescBean.setId(id);
        emcOrderDescBean.setMonitorTmplGroupId(monitorTmplGroupId);
        getDao().insert(emcOrderDescBean);
        return id;
    }

}
