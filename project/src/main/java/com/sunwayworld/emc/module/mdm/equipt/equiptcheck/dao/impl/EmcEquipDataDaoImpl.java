package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipDataBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.dao.EmcEquipDataDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.mapper.EmcEquipDataMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquipDataDaoImpl extends MybatisDaoSupport<EmcEquipDataBean, Long> implements EmcEquipDataDao {

    @Autowired
    private EmcEquipDataMapper emcEquipDataMapper;

    @Override
    public EmcEquipDataMapper getMapper() {
        return emcEquipDataMapper;
    }

}
