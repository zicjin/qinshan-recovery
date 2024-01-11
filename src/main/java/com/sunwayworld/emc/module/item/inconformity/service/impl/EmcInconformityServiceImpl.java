package com.sunwayworld.emc.module.item.inconformity.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.inconformity.bean.EmcInconformityBean;
import com.sunwayworld.emc.module.item.inconformity.dao.EmcInconformityDao;
import com.sunwayworld.emc.module.item.inconformity.service.EmcInconformityService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-12-6
 */

@Repository
@GikamBean
public class EmcInconformityServiceImpl implements EmcInconformityService {
    @Autowired
    private EmcInconformityDao inconformityDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcInconformityDao getDao() {
        return inconformityDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcInconformityBean inconformity = jsonWrapper.parseUnique(EmcInconformityBean.class);
        inconformity.setId(ApplicationContextHelper.getNextIdentity());
        inconformity.setQualityControlNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        getDao().insert(inconformity);
        return inconformity.getId();
    }

}
