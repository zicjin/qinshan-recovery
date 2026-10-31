package com.sunwayworld.emc.module.mdm.basemdm.monitorability.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.monitorability.bean.EmcMonitorAbilityEmpBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.dao.EmcMonitorAbilityEmpDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitorability.mapper.EmcMonitorAbilityEmpMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magt@sunwayworld.com
 * @date 2019-10-09
 */
@Repository
public class EmcMonitorAbilityEmpDaoImpl extends MybatisDaoSupport<EmcMonitorAbilityEmpBean, Long>implements EmcMonitorAbilityEmpDao {
    
    @Autowired
    private EmcMonitorAbilityEmpMapper emcMonitorAbilityEmpMapper;
    
    @Override
    public EmcMonitorAbilityEmpMapper getMapper() {
        return emcMonitorAbilityEmpMapper;
    }
    
}
