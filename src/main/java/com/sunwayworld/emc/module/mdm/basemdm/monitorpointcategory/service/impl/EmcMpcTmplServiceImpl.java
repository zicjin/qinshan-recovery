package com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMpcTmplBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.dao.EmcMpcTmplDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service.EmcMpcTmplService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-10
 */

@Repository
public class EmcMpcTmplServiceImpl implements EmcMpcTmplService {
    @Autowired
    private EmcMpcTmplDao emcMpcTmplDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMpcTmplDao getDao() {
        return emcMpcTmplDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(String monitorPointCategoryId, RestJsonWrapperBean jsonWrapper) {
        List<EmcMpcTmplBean> mpcTmplList = jsonWrapper.parse(EmcMpcTmplBean.class);
        mpcTmplList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setMonitorPointCategoryId(monitorPointCategoryId);
        });
        getDao().insert(mpcTmplList);
        return mpcTmplList.get(0).getId();
    }

}
