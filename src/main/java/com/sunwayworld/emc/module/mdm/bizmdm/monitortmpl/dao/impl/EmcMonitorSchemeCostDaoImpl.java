package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.dao.impl;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeCostBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.dao.EmcMonitorSchemeCostDao;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.mapper.EmcMonitorSchemeCostMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class EmcMonitorSchemeCostDaoImpl extends MybatisDaoSupport<EmcMonitorSchemeCostBean, Long> implements EmcMonitorSchemeCostDao {
    @Autowired
    private EmcMonitorSchemeCostMapper emcMonitorSchemeCostMapper;

    @Override
    public EmcMonitorSchemeCostMapper getMapper() {
        return emcMonitorSchemeCostMapper;
    }
}
