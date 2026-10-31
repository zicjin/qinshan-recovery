package com.sunwayworld.emc.module.bp.info.infodata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.info.infodata.bean.EmcInfoDataItemBean;
import com.sunwayworld.emc.module.bp.info.infodata.dao.EmcInfoDataItemDao;
import com.sunwayworld.emc.module.bp.info.infodata.service.EmcInfoDataItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;


@Repository
@GikamBean
public class EmcInfoDataItemServiceImpl implements EmcInfoDataItemService {
    @Autowired
    private EmcInfoDataItemDao infoDataItemDao;
    
    @Override
    @SuppressWarnings("unchecked")
    public EmcInfoDataItemDao getDao() {
        return infoDataItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT) 
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcInfoDataItemBean emcInfoDataItem = jsonWrapper.parseUnique(EmcInfoDataItemBean.class);
        emcInfoDataItem.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcInfoDataItem);
        return emcInfoDataItem.getId();
    }
    
}
