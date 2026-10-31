package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipCheckBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.dao.EmcEquipCheckDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.mapper.EmcEquipCheckMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquipCheckDaoImpl extends MybatisDaoSupport<EmcEquipCheckBean, Long> implements EmcEquipCheckDao {

    @Autowired
    private EmcEquipCheckMapper emcEquipCheckMapper;

    @Override
    public EmcEquipCheckMapper getMapper() {
        return emcEquipCheckMapper;
    }

}
