package com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptDCUConfBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptDCUConfDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptDCUConfService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptDCUConfServiceImpl implements EmcEquiptDCUConfService {

    @Autowired
    private EmcEquiptDCUConfDao emcEquiptDCUConfDao;

    @SuppressWarnings("unchecked")
    @Override
    public EmcEquiptDCUConfDao getDao() {
        return emcEquiptDCUConfDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertWithEquipt(Long equiptId) {
        EmcEquiptDCUConfBean equiptDCUConfBean = new EmcEquiptDCUConfBean();
        equiptDCUConfBean.setId(equiptId);
        getDao().insert(equiptDCUConfBean);
    }

}
