package com.sunwayworld.emc.module.item.leaveapplies.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.leaveapplies.bean.EmcLeaveApplyBean;
import com.sunwayworld.emc.module.item.leaveapplies.dao.EmcLeaveApplyDao;
import com.sunwayworld.emc.module.item.leaveapplies.service.EmcLeaveApplyService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-12
 */

@Repository
@GikamBean
public class EmcLeaveApplyServiceImpl implements EmcLeaveApplyService {
    @Autowired
    private EmcLeaveApplyDao leaveApplyDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcLeaveApplyDao getDao() {
        return leaveApplyDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean jsonWrapper) {
        EmcLeaveApplyBean leaveApply = jsonWrapper.parseUnique(EmcLeaveApplyBean.class);
        leaveApply.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        getDao().insert(leaveApply);
        return leaveApply.getId();
    }

}
