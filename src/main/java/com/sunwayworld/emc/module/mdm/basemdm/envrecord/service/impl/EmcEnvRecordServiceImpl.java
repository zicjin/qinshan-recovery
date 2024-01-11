package com.sunwayworld.emc.module.mdm.basemdm.envrecord.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.envrecord.bean.EmcEnvRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.envrecord.dao.EmcEnvRecordDao;
import com.sunwayworld.emc.module.mdm.basemdm.envrecord.service.EmcEnvRecordService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEnvRecordServiceImpl implements EmcEnvRecordService {

    @Autowired
    private EmcEnvRecordDao envRecordDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcEnvRecordDao getDao() {
        return envRecordDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean wrapper) {
        EmcEnvRecordBean envRecordBean = wrapper.parseUnique(EmcEnvRecordBean.class);
        envRecordBean.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        getDao().insert(envRecordBean);
                
        return envRecordBean.getId();
    }

}