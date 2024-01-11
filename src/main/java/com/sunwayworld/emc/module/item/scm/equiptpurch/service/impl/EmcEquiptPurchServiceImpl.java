package com.sunwayworld.emc.module.item.scm.equiptpurch.service.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.scm.equiptpurch.bean.EmcEquiptPurchBean;
import com.sunwayworld.emc.module.item.scm.equiptpurch.dao.EmcEquiptPurchDao;
import com.sunwayworld.emc.module.item.scm.equiptpurch.service.EmcEquiptPurchService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcEquiptPurchServiceImpl implements EmcEquiptPurchService {
    @Autowired
    private EmcEquiptPurchDao emcEquiptPurchDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptPurchDao getDao() {
        return emcEquiptPurchDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquiptPurchBean emcEquiptPurch = jsonWrapper.parseUnique(EmcEquiptPurchBean.class);
        emcEquiptPurch.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcEquiptPurch);
        return emcEquiptPurch.getId();
    }

}
