package com.sunwayworld.emc.module.bp.auditrecord.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.auditrecord.bean.EmcAuditRecordBean;
import com.sunwayworld.emc.module.bp.auditrecord.dao.EmcAuditRecordDao;
import com.sunwayworld.emc.module.bp.auditrecord.service.EmcAuditRecordService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-11-07
 */

@Repository
public class EmcAuditRecordServiceImpl implements EmcAuditRecordService {
    @Autowired
    private EmcAuditRecordDao auditRecordDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcAuditRecordDao getDao() {
        return auditRecordDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcAuditRecordBean emcAuditRecord = jsonWrapper.parseUnique(EmcAuditRecordBean.class);
        emcAuditRecord.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcAuditRecord);
        return emcAuditRecord.getId();
    }
}
