package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.dao.EmcEquiptCalibrationDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.mapper.EmcEquiptCalibrationMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2020年08月09日
*/
@Repository
public class EmcEquiptCalibrationDaoImpl extends MybatisDaoSupport<EmcEquiptCalibrationBean, Long> implements EmcEquiptCalibrationDao {

    @Autowired
    private EmcEquiptCalibrationMapper emcEquiptCalibrationMapper;

    @Override
    public EmcEquiptCalibrationMapper getMapper() {
        return emcEquiptCalibrationMapper;
    }
}
