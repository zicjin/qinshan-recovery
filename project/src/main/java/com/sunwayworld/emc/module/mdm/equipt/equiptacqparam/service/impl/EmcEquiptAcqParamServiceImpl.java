package com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.bean.EmcEquiptAcqParamBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.dao.EmcEquiptAcqParamDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqparam.service.EmcEquiptAcqParamService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptAcqParamServiceImpl implements EmcEquiptAcqParamService {

    @Autowired
    private EmcEquiptAcqParamDao emcEquiptAcqParamDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcEquiptAcqParamDao getDao() {
        return emcEquiptAcqParamDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {

        EmcEquiptAcqParamBean equiptAcqParam = jsonWrapper.parseUnique(EmcEquiptAcqParamBean.class);
        equiptAcqParam.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(equiptAcqParam);
        return equiptAcqParam.getId();

    }

}
