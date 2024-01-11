package com.sunwayworld.emc.module.item.internalaudit.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.internalaudit.bean.EmcInternalAuditBean;
import com.sunwayworld.emc.module.item.internalaudit.dao.EmcInternalAuditDao;
import com.sunwayworld.emc.module.item.internalaudit.service.EmcInternalAuditService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */

@Repository
@GikamBean
public class EmcInternalAuditServiceImpl implements EmcInternalAuditService {
    @Autowired
    private EmcInternalAuditDao internalAuditDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcInternalAuditDao getDao() {
        return internalAuditDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcInternalAuditBean internalAudit = jsonWrapper.parseUnique(EmcInternalAuditBean.class);
        internalAudit.setId(ApplicationContextHelper.getNextIdentity());
        internalAudit.setQualityControlNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        getDao().insert(internalAudit);
        return internalAudit.getId();
    }

}
