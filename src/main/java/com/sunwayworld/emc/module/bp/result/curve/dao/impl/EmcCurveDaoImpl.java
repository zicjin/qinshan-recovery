package com.sunwayworld.emc.module.bp.result.curve.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.curve.bean.EmcCurveBean;
import com.sunwayworld.emc.module.bp.result.curve.dao.EmcCurveDao;
import com.sunwayworld.emc.module.bp.result.curve.mapper.EmcCurveMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-11-12
 */
@Repository
public class EmcCurveDaoImpl extends MybatisDaoSupport<EmcCurveBean, Long>implements EmcCurveDao {
    
    @Autowired
    private EmcCurveMapper emcCurveMapper;
    
    @Override
    public EmcCurveMapper getMapper() {
        return emcCurveMapper;
    }
    
}
