package com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.bean.EmcEquiptCaliResultNormBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.dao.EmcEquiptCaliResultNormDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.service.EmcEquiptCaliResultNormService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptCaliResultNormServiceImpl implements EmcEquiptCaliResultNormService {

    @Autowired
    private EmcEquiptCaliResultNormDao emcEquiptCaliResultNormDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptCaliResultNormDao getDao() {
        return emcEquiptCaliResultNormDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long id, RestJsonWrapperBean jsonWrapper) {
        List<EmcEquiptCaliResultNormBean> emcEquiptTestMethodList = jsonWrapper.parse(EmcEquiptCaliResultNormBean.class);
        emcEquiptTestMethodList.stream().forEach(e ->{
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setCaliResultId(id);
        });
        getDao().insert(emcEquiptTestMethodList);
        return emcEquiptTestMethodList.get(0).getId();
    }

}
