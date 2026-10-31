package com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.bean.EmcEquiptCaliResultNormBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.dao.EmcEquiptCaliResultNormDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.mapper.EmcEquiptCaliResultNormMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptCaliResultNormDaoImpl extends MybatisDaoSupport<EmcEquiptCaliResultNormBean, Long> implements EmcEquiptCaliResultNormDao {

    @Autowired
    private EmcEquiptCaliResultNormMapper emcEquiptCaliResultNormMapper;

    @Override
    public EmcEquiptCaliResultNormMapper getMapper() {
        return emcEquiptCaliResultNormMapper;
    }

}
