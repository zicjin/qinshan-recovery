package com.sunwayworld.emc.module.mdm.basemdm.monitorability.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityTestBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.dao.EmcMonitorAbilityTestDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.mapper.EmcMonitorAbilityTestMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */
@Repository
public class EmcMonitorAbilityTestDaoImpl extends MybatisDaoSupport<EmcMonitorAbilityTestBean, Long>implements EmcMonitorAbilityTestDao {
    
    @Autowired
    private EmcMonitorAbilityTestMapper emcMonitorAbilityTestMapper;
    
    @Override
    public EmcMonitorAbilityTestMapper getMapper() {
        return emcMonitorAbilityTestMapper;
    }
    
}
