package com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.bean.EmcEquiptCalibrationLineBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.dao.EmcEquiptCalibrationLineDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibration.mapper.EmcEquiptCalibrationLineMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2020年08月09日
*/
@Repository
public class EmcEquiptCalibrationLineDaoImpl extends MybatisDaoSupport<EmcEquiptCalibrationLineBean, Long> implements EmcEquiptCalibrationLineDao {

    @Autowired
    private EmcEquiptCalibrationLineMapper emcEquiptCalibrationLineMapper;

    @Override
    public EmcEquiptCalibrationLineMapper getMapper() {
        return emcEquiptCalibrationLineMapper;
    }
}
