package com.sunwayworld.emc.module.mdm.basemdm.subcontractor.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.bean.EmcSubcontractorBean;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.dao.EmcSubcontractorDao;
import com.sunwayworld.emc.module.mdm.basemdm.subcontractor.mapper.EmcSubcontractorMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magh@sunwayworld.com
 * @date 2019-11-05
 */
@Repository
public class EmcSubcontractorDaoImpl extends MybatisDaoSupport<EmcSubcontractorBean, Long>implements EmcSubcontractorDao {
    
    @Autowired
    private EmcSubcontractorMapper emcSubcontractorMapper;
    
    @Override
    public EmcSubcontractorMapper getMapper() {
        return emcSubcontractorMapper;
    }
    
}
