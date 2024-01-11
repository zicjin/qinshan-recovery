package com.sunwayworld.emc.module.mdm.bizmdm.client.dao.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.dao.EmcMonitorPointSchemeDao;
import com.sunwayworld.emc.module.mdm.bizmdm.client.mapper.EmcMonitorPointSchemeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */
@Repository
@GikamBean
public class EmcMonitorPointSchemeDaoImpl extends MybatisDaoSupport<EmcMonitorPointSchemeBean, Long>implements EmcMonitorPointSchemeDao {
    
    @Autowired
    private EmcMonitorPointSchemeMapper emcMonitorPointSchemeMapper;
    
    @Override
    public EmcMonitorPointSchemeMapper getMapper() {
        return emcMonitorPointSchemeMapper;
    }
    
}
