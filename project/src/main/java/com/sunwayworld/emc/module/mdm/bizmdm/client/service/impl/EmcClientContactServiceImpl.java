package com.sunwayworld.emc.module.mdm.bizmdm.client.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientContactBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.dao.EmcClientContactDao;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcClientContactService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

@Repository
public class EmcClientContactServiceImpl implements EmcClientContactService {
    @Autowired
    private EmcClientContactDao emcClientContactDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcClientContactDao getDao() {
        return emcClientContactDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(Long clientId, RestJsonWrapperBean jsonWrapper) {
        EmcClientContactBean emcClientContact = jsonWrapper.parseUnique(EmcClientContactBean.class);
        emcClientContact.setId(ApplicationContextHelper.getNextSequenceNumber(getDao().getTable()));
        emcClientContact.setClientId(clientId);
        getDao().insert(emcClientContact);
    }

}
