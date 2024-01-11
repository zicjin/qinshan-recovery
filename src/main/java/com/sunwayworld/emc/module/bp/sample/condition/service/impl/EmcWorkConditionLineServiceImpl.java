package com.sunwayworld.emc.module.bp.sample.condition.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.sample.condition.bean.EmcWorkConditionLineBean;
import com.sunwayworld.emc.module.bp.sample.condition.dao.EmcWorkConditionLineDao;
import com.sunwayworld.emc.module.bp.sample.condition.service.EmcWorkConditionLineService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcWorkConditionLineServiceImpl implements EmcWorkConditionLineService {

    @Autowired
    private EmcWorkConditionLineDao emcWorkConditionLineDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcWorkConditionLineDao getDao() {
        return emcWorkConditionLineDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcWorkConditionLineBean workCondition = jsonWrapper.parseUnique(EmcWorkConditionLineBean.class);
        workCondition.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(workCondition);
        return workCondition.getId();
    }

}
