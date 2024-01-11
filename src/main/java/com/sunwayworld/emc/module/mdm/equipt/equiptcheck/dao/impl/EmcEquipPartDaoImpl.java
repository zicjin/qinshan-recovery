package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipPartBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.dao.EmcEquipPartDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.mapper.EmcEquipPartMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquipPartDaoImpl extends MybatisDaoSupport<EmcEquipPartBean, Long> implements EmcEquipPartDao {

    @Autowired
    private EmcEquipPartMapper emcEquipPartMapper;

    @Override
    public EmcEquipPartMapper getMapper() {
        return emcEquipPartMapper;
    }

}
