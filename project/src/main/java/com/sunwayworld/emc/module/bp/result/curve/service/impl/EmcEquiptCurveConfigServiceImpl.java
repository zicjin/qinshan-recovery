package com.sunwayworld.emc.module.bp.result.curve.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.result.curve.bean.EmcEquiptCurveConfigBean;
import com.sunwayworld.emc.module.bp.result.curve.dao.EmcEquiptCurveConfigDao;
import com.sunwayworld.emc.module.bp.result.curve.service.EmcEquiptCurveConfigService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;

@Repository
@GikamBean
public class EmcEquiptCurveConfigServiceImpl implements EmcEquiptCurveConfigService {

    @Autowired
    private EmcEquiptCurveConfigDao emcEquiptCurveConfigDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptCurveConfigDao getDao() {
        return emcEquiptCurveConfigDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquiptCurveConfigBean emcEquiptCurveConfig = jsonWrapper.parseUnique(EmcEquiptCurveConfigBean.class);
        emcEquiptCurveConfig.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcEquiptCurveConfig);
        return emcEquiptCurveConfig.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long equiptCurveId, RestJsonWrapperBean wrapper) {
        EmcEquiptCurveConfigBean equiptCurveConfig = new EmcEquiptCurveConfigBean();
        equiptCurveConfig.setId(ApplicationContextHelper.getNextIdentity());
        equiptCurveConfig.setEquiptCurveId(equiptCurveId);
        getDao().insert(equiptCurveConfig);
        return equiptCurveConfig.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyEquiptCurveConfig(RestJsonWrapperBean wrapper) {
        EmcEquiptCurveConfigBean equiptConfig = wrapper.parseUnique(EmcEquiptCurveConfigBean.class);

        EmcEquiptCurveConfigBean newEquiptConfig = new EmcEquiptCurveConfigBean();

        BeanUtils.copyProperties(equiptConfig, newEquiptConfig, "ID");

        newEquiptConfig.setId(ApplicationContextHelper.getNextIdentity());

        getDao().insert(newEquiptConfig);
    }
}
