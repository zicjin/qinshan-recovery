package com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquipCertBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquipCertDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquipCertService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquipCertServiceImpl implements EmcEquipCertService {

    @Autowired
    private EmcEquipCertDao emcEquipCertDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquipCertDao getDao() {
        return emcEquipCertDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertEquipCert(String equipId,RestJsonWrapperBean jsonWrapper) {
        List<EmcEquipCertBean> equipCertList = jsonWrapper.parse(EmcEquipCertBean.class);

        equipCertList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setEquiptId(equipId);
        });

        getDao().insert(equipCertList);
    }

}
