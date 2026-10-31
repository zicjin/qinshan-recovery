package com.sunwayworld.emc.module.item.precaution.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.precaution.bean.EmcPrecautionBean;
import com.sunwayworld.emc.module.item.precaution.dao.EmcPrecautionDao;
import com.sunwayworld.emc.module.item.precaution.service.EmcPrecautionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-25
 */

@Repository
@GikamBean
public class EmcPrecautionServiceImpl implements EmcPrecautionService {
    @Autowired
    private EmcPrecautionDao precautionDao;
    
    @Override
    @SuppressWarnings("unchecked")
    public EmcPrecautionDao getDao() {
        return precautionDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT) 
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcPrecautionBean precaution = jsonWrapper.parseUnique(EmcPrecautionBean.class);
        precaution.setId(ApplicationContextHelper.getNextIdentity());
        precaution.setPrecauteCode(ApplicationContextHelper.getNextSequence("T_EMC_PRECAUTION", "YFCS"));
        getDao().insert(precaution);
        return precaution.getId();
    }
    
}
