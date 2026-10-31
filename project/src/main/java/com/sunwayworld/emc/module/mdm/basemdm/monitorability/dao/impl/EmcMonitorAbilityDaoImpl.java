package com.sunwayworld.emc.module.mdm.basemdm.monitorability.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.dao.EmcMonitorAbilityDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.mapper.EmcMonitorAbilityMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */
@Repository
public class EmcMonitorAbilityDaoImpl extends MybatisDaoSupport<EmcMonitorAbilityBean, Long>implements EmcMonitorAbilityDao {
    
    @Autowired
    private EmcMonitorAbilityMapper emcMonitorAbilityMapper;
    
    @Override
    public EmcMonitorAbilityMapper getMapper() {
        return emcMonitorAbilityMapper;
    }
    
}
