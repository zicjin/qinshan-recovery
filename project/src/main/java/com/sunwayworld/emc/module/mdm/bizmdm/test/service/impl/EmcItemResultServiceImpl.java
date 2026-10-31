package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcItemResultDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemResultService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@Repository
public class EmcItemResultServiceImpl implements EmcItemResultService {
    @Autowired
    private EmcItemResultDao emcItemResultDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcItemResultDao getDao() {
        return emcItemResultDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long itemId, RestJsonWrapperBean jsonWrapper) {
        EmcItemResultBean emcItemResult = jsonWrapper.parseUnique(EmcItemResultBean.class);
        emcItemResult.setId(ApplicationContextHelper.getNextIdentity());
        emcItemResult.setItemId(itemId);
        getDao().insert(emcItemResult);
        return emcItemResult.getId();
    }

}
