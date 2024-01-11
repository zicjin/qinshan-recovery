package com.sunwayworld.emc.module.item.qualitycontrol.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.qualitycontrol.bean.EmcQualityControlBean;
import com.sunwayworld.emc.module.item.qualitycontrol.dao.EmcQualityControlDao;
import com.sunwayworld.emc.module.item.qualitycontrol.service.EmcQualityControlService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magt@sunwayworld.com
 * @date 2019-12-04
 */

@Repository
@GikamBean
public class EmcQualityControlServiceImpl implements EmcQualityControlService {
    
    @Autowired
    private EmcQualityControlDao qualityControlDao;
    
    @SuppressWarnings("unchecked")
    @Override
    public EmcQualityControlDao getDao() {
        return qualityControlDao;
    }
    
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcQualityControlBean emcQualityControl = jsonWrapper.parseUnique(EmcQualityControlBean.class);
        emcQualityControl.setId(ApplicationContextHelper.getNextIdentity());
        emcQualityControl.setQualityControlCode(ApplicationContextHelper.getNextSequence(getDao().getTable(),"ZKJH"));
        getDao().insert(emcQualityControl);
        return emcQualityControl.getId();
    }
}
