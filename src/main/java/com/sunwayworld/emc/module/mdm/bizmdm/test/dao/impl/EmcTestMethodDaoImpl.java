package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestMethodDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcTestMethodMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;


/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */
@Repository
public class EmcTestMethodDaoImpl extends MybatisDaoSupport<EmcTestMethodBean, Long>implements EmcTestMethodDao {
    
    @Autowired
    private EmcTestMethodMapper emcTestMethodMapper;
    
    @Override
    public EmcTestMethodMapper getMapper() {
        return emcTestMethodMapper;
    }

}
