package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcTestMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author magh@sunwayworld.com
 * @date 2019-10-18
 */
@Repository
public class EmcTestDaoImpl extends MybatisDaoSupport<EmcTestBean, Long>implements EmcTestDao {
    
    @Autowired
    private EmcTestMapper emcTestMapper;
    
    @Override
    public EmcTestMapper getMapper() {
        return emcTestMapper;
    }
    
}
