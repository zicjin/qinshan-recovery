package com.sunwayworld.emc.module.bp.sample.condition.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.sample.condition.bean.EmcWorkConditionBean;
import com.sunwayworld.emc.module.bp.sample.condition.bean.EmcWorkConditionLineBean;
import com.sunwayworld.emc.module.bp.sample.condition.dao.EmcWorkConditionDao;
import com.sunwayworld.emc.module.bp.sample.condition.service.EmcWorkConditionLineService;
import com.sunwayworld.emc.module.bp.sample.condition.service.EmcWorkConditionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcWorkConditionServiceImpl implements EmcWorkConditionService {

    @Autowired
    private EmcWorkConditionLineService workConditionLineService;

    @Autowired
    private EmcWorkConditionDao emcWorkConditionDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcWorkConditionDao getDao() {
        return emcWorkConditionDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcWorkConditionBean emcWorkCondition = jsonWrapper.parseUnique(EmcWorkConditionBean.class);
        emcWorkCondition.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcWorkCondition);
        return emcWorkCondition.getId();
    }

    @Override
    public Page<EmcWorkConditionLineBean> queryLineByPagination(RestJsonWrapperBean wrapper) {
        return workConditionLineService.selectPagination(wrapper);
    }

    @Override
    public void insertLine(RestJsonWrapperBean wrapper) {
        workConditionLineService.insert(wrapper);
    }

    @Override
    public void deleteLine(RestJsonWrapperBean wrapper) {
        workConditionLineService.delete(wrapper);
    }
}
