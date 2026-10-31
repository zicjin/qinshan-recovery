package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeSamplingCostBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.dao.EmcMonitorSchemeSamplingCostDao;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.mapper.EmcMonitorSchemeSamplingCostMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcMonitorSchemeSamplingCostDaoImpl extends MybatisDaoSupport<EmcMonitorSchemeSamplingCostBean, Long> implements EmcMonitorSchemeSamplingCostDao {
    @Autowired
    private EmcMonitorSchemeSamplingCostMapper schemeSamplingCostMapper;

    @Override
    public EmcMonitorSchemeSamplingCostMapper getMapper() {
        return schemeSamplingCostMapper;
    }
}
