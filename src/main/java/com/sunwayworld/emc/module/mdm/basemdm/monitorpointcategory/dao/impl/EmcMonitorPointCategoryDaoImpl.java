package com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMonitorPointCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.dao.EmcMonitorPointCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.mapper.EmcMonitorPointCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magh@sunwayworld.com
 * @date 2019-09-10
 */
@Repository
public class EmcMonitorPointCategoryDaoImpl extends MybatisDaoSupport<EmcMonitorPointCategoryBean, String>implements EmcMonitorPointCategoryDao {
    
    @Autowired
    private EmcMonitorPointCategoryMapper emcMonitorPointCategoryMapper;
    
    @Override
    public EmcMonitorPointCategoryMapper getMapper() {
        return emcMonitorPointCategoryMapper;
    }
    
}
