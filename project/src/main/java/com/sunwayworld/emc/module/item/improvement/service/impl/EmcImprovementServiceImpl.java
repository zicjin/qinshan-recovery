package com.sunwayworld.emc.module.item.improvement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.item.improvement.bean.EmcImprovementBean;
import com.sunwayworld.emc.module.item.improvement.dao.EmcImprovementDao;
import com.sunwayworld.emc.module.item.improvement.service.EmcImprovementService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author guok@sunwayworld.com
 * @date 2019-12-09
 */

@Repository
@GikamBean
public class EmcImprovementServiceImpl implements EmcImprovementService {
    @Autowired
    private EmcImprovementDao improvementDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcImprovementDao getDao() {
        return improvementDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcImprovementBean improvement = jsonWrapper.parseUnique(EmcImprovementBean.class);
        improvement.setId(ApplicationContextHelper.getNextIdentity());
        improvement.setQualityControlNo(ApplicationContextHelper.getNextSequence(getDao().getTable()));
        getDao().insert(improvement);
        return improvement.getId();
    }
}
