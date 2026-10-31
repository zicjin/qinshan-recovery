package com.sunwayworld.emc.module.mdm.basemdm.cert.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertMonitorAbilityBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.dao.EmcCertMonitorAbilityDao;
import com.sunwayworld.emc.module.mdm.basemdm.cert.mapper.EmcCertMonitorAbilityMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */
@Repository
public class EmcCertMonitorAbilityDaoImpl extends MybatisDaoSupport<EmcCertMonitorAbilityBean, Long>implements EmcCertMonitorAbilityDao {
    
    @Autowired
    private EmcCertMonitorAbilityMapper emcCertMonitorAbilityMapper;
    
    @Override
    public EmcCertMonitorAbilityMapper getMapper() {
        return emcCertMonitorAbilityMapper;
    }
    
}
