package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipAppearanceBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.dao.EmcEquipAppearanceDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.mapper.EmcEquipAppearanceMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquipAppearanceDaoImpl extends MybatisDaoSupport<EmcEquipAppearanceBean, Long> implements EmcEquipAppearanceDao {

    @Autowired
    private EmcEquipAppearanceMapper emcEquipAppearanceMapper;

    @Override
    public EmcEquipAppearanceMapper getMapper() {
        return emcEquipAppearanceMapper;
    }

}
