package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodEquiptBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestMethodEquiptDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcTestMethodEquiptMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */
@Repository
public class EmcTestMethodEquiptDaoImpl extends MybatisDaoSupport<EmcTestMethodEquiptBean, Long>implements EmcTestMethodEquiptDao {
    
    @Autowired
    private EmcTestMethodEquiptMapper emcTestMethodEquiptMapper;
    
    @Override
    public EmcTestMethodEquiptMapper getMapper() {
        return emcTestMethodEquiptMapper;
    }
    
}
