package com.sunwayworld.emc.module.mdm.equipt.filescript.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.filescript.bean.EmcEquiptFileScriptBean;
import com.sunwayworld.emc.module.mdm.equipt.filescript.dao.EmcEquiptFileScriptDao;
import com.sunwayworld.emc.module.mdm.equipt.filescript.service.EmcEquiptFileScriptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptFileScriptServiceImpl implements EmcEquiptFileScriptService {
    @Autowired
    private EmcEquiptFileScriptDao equiptFileScriptDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptFileScriptDao getDao() {
        return equiptFileScriptDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcEquiptFileScriptBean equiptFileScriptBean = wrapper.parseUnique(EmcEquiptFileScriptBean.class);
        equiptFileScriptBean.setId(ApplicationContextHelper.getNextSequenceNumber(getDao().getTable()));
        getDao().insert(equiptFileScriptBean);
        return equiptFileScriptBean.getId();
    }
}