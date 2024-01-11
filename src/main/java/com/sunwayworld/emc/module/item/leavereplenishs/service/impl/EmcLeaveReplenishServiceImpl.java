package com.sunwayworld.emc.module.item.leavereplenishs.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.leavereplenishs.bean.EmcLeaveReplenishBean;
import com.sunwayworld.emc.module.item.leavereplenishs.dao.EmcLeaveReplenishDao;
import com.sunwayworld.emc.module.item.leavereplenishs.service.EmcLeaveReplenishService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-14
 */

@Repository
@GikamBean
public class EmcLeaveReplenishServiceImpl implements EmcLeaveReplenishService {
    @Autowired
    private EmcLeaveReplenishDao leaveReplenishDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcLeaveReplenishDao getDao() {
        return leaveReplenishDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String insert(RestJsonWrapperBean jsonWrapper) {
        EmcLeaveReplenishBean leaveReplenish = jsonWrapper.parseUnique(EmcLeaveReplenishBean.class);
        leaveReplenish.setId(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        getDao().insert(leaveReplenish);
        return leaveReplenish.getId();
    }

}
