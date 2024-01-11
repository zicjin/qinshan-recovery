package com.sunwayworld.emc.module.item.car.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.car.bean.EmcCarBean;
import com.sunwayworld.emc.module.item.car.dao.EmcCarDao;
import com.sunwayworld.emc.module.item.car.service.EmcCarService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */

@Repository
@GikamBean
public class EmcCarServiceImpl implements EmcCarService {
    @Autowired
    private EmcCarDao emcCarDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcCarDao getDao() {
        return emcCarDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcCarBean emcCar = jsonWrapper.parseUnique(EmcCarBean.class);
        emcCar.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcCar);
        return emcCar.getId();
    }

}
