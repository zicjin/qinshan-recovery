package com.sunwayworld.emc.module.mdm.basemdm.fixedasset.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.fixedasset.bean.EmcFixedassetBean;
import com.sunwayworld.emc.module.mdm.basemdm.fixedasset.dao.EmcFixedassetDao;
import com.sunwayworld.emc.module.mdm.basemdm.fixedasset.service.EmcFixedassetService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-25
 */

@Repository
public class EmcFixedassetServiceImpl implements EmcFixedassetService {
    @Autowired
    private EmcFixedassetDao emcFixedassetDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcFixedassetDao getDao() {
        return emcFixedassetDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcFixedassetBean emcFixedasset = jsonWrapper.parseUnique(EmcFixedassetBean.class);
        emcFixedasset.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcFixedasset);
        return emcFixedasset.getId();
    }

}
