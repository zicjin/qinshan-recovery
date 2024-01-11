package com.sunwayworld.emc.module.mdm.equipt.verification.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.verification.bean.EmcEquipVerificationBean;
import com.sunwayworld.emc.module.mdm.equipt.verification.dao.EmcEquipVerificationDao;
import com.sunwayworld.emc.module.mdm.equipt.verification.mapper.EmcEquipVerificationMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquipVerificationDaoImpl extends MybatisDaoSupport<EmcEquipVerificationBean, Long> implements EmcEquipVerificationDao {

    @Autowired
    private EmcEquipVerificationMapper emcEquipVerificationMapper;

    @Override
    public EmcEquipVerificationMapper getMapper() {
        return emcEquipVerificationMapper;
    }

}
