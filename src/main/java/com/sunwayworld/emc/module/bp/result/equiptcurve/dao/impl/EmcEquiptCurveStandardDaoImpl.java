package com.sunwayworld.emc.module.bp.result.equiptcurve.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.equiptcurve.bean.EmcEquiptCurveStandardBean;
import com.sunwayworld.emc.module.bp.result.equiptcurve.dao.EmcEquiptCurveStandardDao;
import com.sunwayworld.emc.module.bp.result.equiptcurve.mapper.EmcEquiptCurveStandardMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptCurveStandardDaoImpl extends MybatisDaoSupport<EmcEquiptCurveStandardBean, Long> implements EmcEquiptCurveStandardDao {

    @Autowired
    private EmcEquiptCurveStandardMapper emcEquiptCurveStandardMapper;

    @Override
    public EmcEquiptCurveStandardMapper getMapper() {
        return emcEquiptCurveStandardMapper;
    }

}
