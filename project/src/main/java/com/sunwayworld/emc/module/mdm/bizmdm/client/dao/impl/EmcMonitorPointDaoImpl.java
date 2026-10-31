package com.sunwayworld.emc.module.mdm.bizmdm.client.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.dao.EmcMonitorPointDao;
import com.sunwayworld.emc.module.mdm.bizmdm.client.mapper.EmcMonitorPointMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */
@Repository
public class EmcMonitorPointDaoImpl extends MybatisDaoSupport<EmcMonitorPointBean, Long>implements EmcMonitorPointDao {
    
    @Autowired
    private EmcMonitorPointMapper emcMonitorPointMapper;
    
    @Override
    public EmcMonitorPointMapper getMapper() {
        return emcMonitorPointMapper;
    }
    
}
