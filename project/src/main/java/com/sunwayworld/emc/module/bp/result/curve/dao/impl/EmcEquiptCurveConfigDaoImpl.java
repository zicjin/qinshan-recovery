package com.sunwayworld.emc.module.bp.result.curve.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.curve.bean.EmcEquiptCurveConfigBean;
import com.sunwayworld.emc.module.bp.result.curve.dao.EmcEquiptCurveConfigDao;
import com.sunwayworld.emc.module.bp.result.curve.mapper.EmcEquiptCurveConfigMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptCurveConfigDaoImpl extends MybatisDaoSupport<EmcEquiptCurveConfigBean, Long> implements EmcEquiptCurveConfigDao {

    @Autowired
    private EmcEquiptCurveConfigMapper emcEquiptCurveConfigMapper;

    @Override
    public EmcEquiptCurveConfigMapper getMapper() {
        return emcEquiptCurveConfigMapper;
    }

}
