package com.sunwayworld.emc.module.bp.result.equiptcurve.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.bp.result.equiptcurve.dao.EmcEquiptCurveStandardDao;
import com.sunwayworld.emc.module.bp.result.equiptcurve.bean.EmcEquiptCurveStandardBean;
import com.sunwayworld.emc.module.bp.result.equiptcurve.service.EmcEquiptCurveStandardService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
@GikamBean
public class EmcEquiptCurveStandardServiceImpl implements EmcEquiptCurveStandardService {

    @Autowired
    private EmcEquiptCurveStandardDao emcEquiptCurveStandardDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptCurveStandardDao getDao() {
        return emcEquiptCurveStandardDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquiptCurveStandardBean emcEquiptCurveStandard = jsonWrapper.parseUnique(EmcEquiptCurveStandardBean.class);
        emcEquiptCurveStandard.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcEquiptCurveStandard);
        return emcEquiptCurveStandard.getId();
    }

}
