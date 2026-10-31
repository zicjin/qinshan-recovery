package com.sunwayworld.emc.module.bp.result.equiptcurve.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.equiptcurve.bean.EmcEquiptCurveBean;
import com.sunwayworld.emc.module.bp.result.equiptcurve.dao.EmcEquiptCurveDao;
import com.sunwayworld.emc.module.bp.result.equiptcurve.mapper.EmcEquiptCurveMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptCurveDaoImpl extends MybatisDaoSupport<EmcEquiptCurveBean, Long> implements EmcEquiptCurveDao {

    @Autowired
    private EmcEquiptCurveMapper emcEquiptCurveMapper;

    @Override
    public EmcEquiptCurveMapper getMapper() {
        return emcEquiptCurveMapper;
    }

}
