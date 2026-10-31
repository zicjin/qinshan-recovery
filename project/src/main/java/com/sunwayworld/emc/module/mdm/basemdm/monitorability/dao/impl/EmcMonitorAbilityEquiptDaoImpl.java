package com.sunwayworld.emc.module.mdm.basemdm.monitorability.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityEquiptBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.dao.EmcMonitorAbilityEquiptDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.mapper.EmcMonitorAbilityEquiptMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */
@Repository
public class EmcMonitorAbilityEquiptDaoImpl extends MybatisDaoSupport<EmcMonitorAbilityEquiptBean, Long>implements EmcMonitorAbilityEquiptDao {
    
    @Autowired
    private EmcMonitorAbilityEquiptMapper emcMonitorAbilityEquiptMapper;
    
    @Override
    public EmcMonitorAbilityEquiptMapper getMapper() {
        return emcMonitorAbilityEquiptMapper;
    }
    
}
