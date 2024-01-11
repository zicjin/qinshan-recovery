package com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.bean.EmcEasParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.dao.EmcEasParamDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqscript.service.EmcEasParamService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEasParamServiceImpl implements EmcEasParamService {

    @Autowired
    private EmcEasParamDao emcEasParamDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcEasParamDao getDao() {
        return emcEasParamDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(Long scriptId, RestJsonWrapperBean wrapper) {
        List<EmcEasParamBean> paramList = wrapper.parse(EmcEasParamBean.class);
        
        paramList.forEach(p -> p.setId(ApplicationContextHelper.getNextIdentity()));
        
        getDao().insert(paramList);
    }
}
