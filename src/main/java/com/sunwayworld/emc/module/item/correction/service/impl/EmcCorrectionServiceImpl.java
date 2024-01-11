package com.sunwayworld.emc.module.item.correction.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.correction.bean.EmcCorrectionBean;
import com.sunwayworld.emc.module.item.correction.dao.EmcCorrectionDao;
import com.sunwayworld.emc.module.item.correction.service.EmcCorrectionService;
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
public class EmcCorrectionServiceImpl implements EmcCorrectionService {
    @Autowired
    private EmcCorrectionDao correctionDao;
    
    @Override
    @SuppressWarnings("unchecked")
    public EmcCorrectionDao getDao() {
        return correctionDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT) 
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcCorrectionBean correction = jsonWrapper.parseUnique(EmcCorrectionBean.class);
        correction.setId(ApplicationContextHelper.getNextIdentity());
        correction.setCorrectionCode(ApplicationContextHelper.getNextSequence("T_EMC_CORRECTION", "JZCS"));
        getDao().insert(correction);
        return correction.getId();
    }
    
}
