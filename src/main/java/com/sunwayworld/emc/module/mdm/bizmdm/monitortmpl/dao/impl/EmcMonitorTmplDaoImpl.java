package com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorTmplBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.dao.EmcMonitorTmplDao;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.mapper.EmcMonitorTmplMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magh@sunwayworld.com
 * @date 2019-09-19
 */
@Repository
public class EmcMonitorTmplDaoImpl extends MybatisDaoSupport<EmcMonitorTmplBean, String>implements EmcMonitorTmplDao {
    
    @Autowired
    private EmcMonitorTmplMapper emcMonitorTmplMapper;
    
    @Override
    public EmcMonitorTmplMapper getMapper() {
        return emcMonitorTmplMapper;
    }
    
}
