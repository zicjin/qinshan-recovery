package com.sunwayworld.emc.module.mdm.basemdm.traincourse.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.traincourse.bean.EmcTcTestMethodBean;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.dao.EmcTcTestMethodDao;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.service.EmcTcTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-28
 */

@Repository
public class EmcTcTestMethodServiceImpl implements EmcTcTestMethodService {
    @Autowired
    private EmcTcTestMethodDao emcTcTestMethodDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcTcTestMethodDao getDao() {
        return emcTcTestMethodDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long id, RestJsonWrapperBean jsonWrapper) {
        List<EmcTcTestMethodBean> tcTestMethodList = jsonWrapper.parse(EmcTcTestMethodBean.class);
        tcTestMethodList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setCourseId(id);
        });
        getDao().insert(tcTestMethodList);
        return tcTestMethodList.get(0).getId();
    }

}
