package com.sunwayworld.emc.module.item.lm.overtime.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.lm.overtime.bean.EmcOvertimeBean;
import com.sunwayworld.emc.module.item.lm.overtime.dao.EmcOvertimeDao;
import com.sunwayworld.emc.module.item.lm.overtime.service.EmcOvertimeService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-14
 */

@Repository
@GikamBean
public class EmcOvertimeServiceImpl implements EmcOvertimeService {
    @Autowired
    private EmcOvertimeDao emcOvertimeDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcOvertimeDao getDao() {
        return emcOvertimeDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcOvertimeBean emcOvertime = jsonWrapper.parseUnique(EmcOvertimeBean.class);
        emcOvertime.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcOvertime);
        return emcOvertime.getId();
    }

}
