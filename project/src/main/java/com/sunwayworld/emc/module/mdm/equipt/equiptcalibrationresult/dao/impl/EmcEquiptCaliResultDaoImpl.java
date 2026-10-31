package com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.bean.EmcEquiptCaliResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.dao.EmcEquiptCaliResultDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.mapper.EmcEquiptCaliResultMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcEquiptCaliResultDaoImpl extends MybatisDaoSupport<EmcEquiptCaliResultBean, Long> implements EmcEquiptCaliResultDao {

    @Autowired
    private EmcEquiptCaliResultMapper emcEquiptCaliResultMapper;

    @Override
    public EmcEquiptCaliResultMapper getMapper() {
        return emcEquiptCaliResultMapper;
    }

}
