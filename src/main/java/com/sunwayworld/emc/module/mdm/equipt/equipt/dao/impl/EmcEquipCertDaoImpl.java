package com.sunwayworld.emc.module.mdm.equipt.equipt.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquipCertBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquipCertDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquipCertMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquipCertDaoImpl extends MybatisDaoSupport<EmcEquipCertBean, Long> implements EmcEquipCertDao {

    @Autowired
    private EmcEquipCertMapper emcEquipCertMapper;

    @Override
    public EmcEquipCertMapper getMapper() {
        return emcEquipCertMapper;
    }

}
